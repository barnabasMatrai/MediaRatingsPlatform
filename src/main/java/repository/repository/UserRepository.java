package repository.repository;

import model.MediaEntry;
import model.User;
import repository.DataAccessException;
import repository.UnitOfWork;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class UserRepository implements IUserRepository {
    private static UserRepository instance = null;

    private UserRepository() {}

    public static UserRepository getInstance() {
        if (instance == null) {
            instance = new UserRepository();
        }

        return instance;
    }

    @Override
    public User get(long id) {
        UnitOfWork unitOfWork = new UnitOfWork();
        try (PreparedStatement preparedStatement = unitOfWork.prepareStatement(
                """
                SELECT username, password, email, first_name, last_name, favorite_genre FROM users
                WHERE id = ?;
                """)) {
            preparedStatement.setLong(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String username = resultSet.getString("username");
                    String password = resultSet.getString("password");
                    String email = resultSet.getString("email");
                    String firstName = resultSet.getString("first_name");
                    String lastName = resultSet.getString("last_name");
                    String favoriteGenre = resultSet.getString("favorite_genre");

                    return new User(username, password, email, firstName, lastName, favoriteGenre);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Problem accessing users.", e);
        }
        return null;
    }

    @Override
    public User get(String username) {
        UnitOfWork unitOfWork = new UnitOfWork();
        try (PreparedStatement preparedStatement = unitOfWork.prepareStatement(
                """
                SELECT password, email, first_name, last_name, favorite_genre FROM users
                WHERE username = ?;
                """)) {
            preparedStatement.setString(1, username);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String password = resultSet.getString("password");
                    String email = resultSet.getString("email");
                    String firstName = resultSet.getString("first_name");
                    String lastName = resultSet.getString("last_name");
                    String favoriteGenre = resultSet.getString("favorite_genre");

                    return new User(username, password, email, firstName, lastName, favoriteGenre);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Problem accessing users.", e);
        }
        return null;
    }

    @Override
    public void add(User user) {
        UnitOfWork unitOfWork = new UnitOfWork();
        try (PreparedStatement preparedStatement = unitOfWork.prepareStatement(
                """
                insert into users(username, password, email, first_name, last_name, favorite_genre)
                values (?, ?, ?, ?, ?, ?);
                """))
        {
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setString(3, user.getEmail());
            preparedStatement.setString(4, user.getFirstName());
            preparedStatement.setString(5, user.getLastName());
            preparedStatement.setString(6, user.getFavoriteGenre());
            preparedStatement.executeUpdate();

            unitOfWork.commitTransaction();
        } catch (SQLException e) {
            unitOfWork.rollbackTransaction();
            throw new DataAccessException("Insert unsuccessful", e);
        }
    }

    @Override
    public void update(long id, User user) {
        UnitOfWork unitOfWork = new UnitOfWork();
        try (PreparedStatement preparedStatement = unitOfWork.prepareStatement(
                """
                UPDATE users
                SET email = ?, favorite_genre = ?
                WHERE id = ?;
                """)) {
            preparedStatement.setString(1, user.getEmail());
            preparedStatement.setString(2, user.getFavoriteGenre());
            preparedStatement.setLong(3, id);
            preparedStatement.executeUpdate();
            unitOfWork.commitTransaction();
        } catch (SQLException e) {
            unitOfWork.rollbackTransaction();
            throw new DataAccessException("Problem with updating user.", e);
        }
    }

    private String[] loadGenresForMedia(UnitOfWork uow, long mediaEntryId) throws SQLException {
        try (PreparedStatement ps =
                     uow.prepareStatement("""
                     SELECT g.name
                     FROM genres g
                     JOIN media_entries_genres meg
                       ON g.id = meg.genre_id
                     WHERE meg.media_entry_id = ?
                     ORDER BY g.name
                 """)) {

            ps.setLong(1, mediaEntryId);

            try (ResultSet rs = ps.executeQuery()) {
                List<String> genres = new ArrayList<>();
                while (rs.next()) {
                    genres.add(rs.getString("name"));
                }
                return genres.toArray(new String[0]);
            }
        }
    }

    @Override
    public List<MediaEntry> getRecommendationsByGenre(long userId) {
        UnitOfWork uow = new UnitOfWork();
        List<MediaEntry> results = new ArrayList<>();

        try {
            String sql = """
            SELECT DISTINCT
                m.id,
                m.title,
                m.description,
                m.release_year,
                m.age_restriction,
                mt.name AS media_type,
                u.username
            FROM media_entries m
            JOIN media_types mt ON m.media_type = mt.id
            JOIN users u ON m.created_by = u.id
            JOIN media_entries_genres meg ON m.id = meg.media_entry_id
            WHERE meg.genre_id IN (
                /* Genres from favorited media */
                SELECT meg1.genre_id
                FROM user_favorites uf
                JOIN media_entries_genres meg1
                    ON uf.media_entry_id = meg1.media_entry_id
                WHERE uf.user_id = ?

                UNION

                /* Genres from highly-rated media */
                SELECT meg2.genre_id
                FROM ratings r
                JOIN media_entries_genres meg2
                    ON r.media_entry_id = meg2.media_entry_id
                WHERE r.created_by = ?
                  AND r.star_value >= 4
            )
            AND m.id NOT IN (
                /* Exclude already favorited */
                SELECT media_entry_id
                FROM user_favorites
                WHERE user_id = ?

                UNION

                /* Exclude already rated */
                SELECT media_entry_id
                FROM ratings
                WHERE created_by = ?
            )
            ORDER BY m.title
        """;

            try (PreparedStatement ps = uow.prepareStatement(sql)) {
                ps.setLong(1, userId);
                ps.setLong(2, userId);
                ps.setLong(3, userId);
                ps.setLong(4, userId);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        long mediaId = rs.getLong("id");
                        String createdBy = rs.getString("username");

                        String[] genres = loadGenresForMedia(uow, mediaId);

                        results.add(new MediaEntry(
                                createdBy,
                                rs.getString("title"),
                                rs.getString("description"),
                                MediaEntry.stringToMediaType(rs.getString("media_type")),
                                rs.getInt("release_year"),
                                genres,
                                rs.getInt("age_restriction")
                        ));
                    }
                }
            }

            uow.commitTransaction();
            return results;

        } catch (Exception e) {
            uow.rollbackTransaction();
            throw new DataAccessException("Failed to fetch recommendations", e);
        }
    }

    @Override
    public List<MediaEntry> getRecommendationsByContent(long userId) {
        UnitOfWork uow = new UnitOfWork();

        try {
            Set<Integer> genreIds = getPreferredGenreIds(uow, userId);
            if (genreIds.isEmpty()) {
                return List.of();
            }

            Set<Integer> mediaTypes = getPreferredMediaTypes(uow, userId);
            int maxAgeRestriction = getMaxAllowedAgeRestriction(uow, userId);
            Set<Long> excludedMediaIds = getExcludedMediaIds(uow, userId);

            List<MediaEntry> recommendations =
                    findRecommendedMedia(
                            uow,
                            genreIds,
                            mediaTypes,
                            maxAgeRestriction,
                            excludedMediaIds
                    );

            uow.commitTransaction();
            return recommendations;

        } catch (Exception e) {
            uow.rollbackTransaction();
            throw new DataAccessException(
                    "Failed to fetch content-based recommendations",
                    e
            );
        }
    }

    private Set<Integer> getPreferredGenreIds(UnitOfWork uow, long userId)
            throws SQLException {

        Set<Integer> genres = new HashSet<>();

        String sql = """
        SELECT DISTINCT meg.genre_id
        FROM media_entries_genres meg
        JOIN user_favorites uf ON meg.media_entry_id = uf.media_entry_id
        WHERE uf.user_id = ?

        UNION

        SELECT DISTINCT meg.genre_id
        FROM media_entries_genres meg
        JOIN ratings r ON meg.media_entry_id = r.media_entry_id
        WHERE r.created_by = ?
          AND r.star_value >= 4
        """;

        try (PreparedStatement ps = uow.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    genres.add(rs.getInt("genre_id"));
                }
            }
        }

        return genres;
    }

    private Set<Integer> getPreferredMediaTypes(UnitOfWork uow, long userId)
            throws SQLException {

        Set<Integer> mediaTypes = new HashSet<>();

        String sql = """
        SELECT DISTINCT m.media_type
        FROM media_entries m
        WHERE m.id IN (
            SELECT media_entry_id FROM user_favorites WHERE user_id = ?
            UNION
            SELECT media_entry_id FROM ratings
            WHERE created_by = ? AND star_value >= 4
        )
        """;

        try (PreparedStatement ps = uow.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    mediaTypes.add(rs.getInt("media_type"));
                }
            }
        }

        return mediaTypes;
    }

    private int getMaxAllowedAgeRestriction(UnitOfWork uow, long userId)
            throws SQLException {

        String sql = """
        SELECT COALESCE(MAX(m.age_restriction), 0) AS max_age
        FROM media_entries m
        WHERE m.id IN (
            SELECT media_entry_id FROM user_favorites WHERE user_id = ?
            UNION
            SELECT media_entry_id FROM ratings
            WHERE created_by = ? AND star_value >= 4
        )
        """;

        try (PreparedStatement ps = uow.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, userId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("max_age") : 0;
            }
        }
    }

    private Set<Long> getExcludedMediaIds(UnitOfWork uow, long userId)
            throws SQLException {

        Set<Long> excluded = new HashSet<>();

        String sql = """
        SELECT media_entry_id FROM user_favorites WHERE user_id = ?
        UNION
        SELECT media_entry_id FROM ratings WHERE created_by = ?
        """;

        try (PreparedStatement ps = uow.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    excluded.add(rs.getLong("media_entry_id"));
                }
            }
        }

        return excluded;
    }

    private List<MediaEntry> findRecommendedMedia(
            UnitOfWork uow,
            Set<Integer> genreIds,
            Set<Integer> mediaTypes,
            int maxAgeRestriction,
            Set<Long> excludedMediaIds
    ) throws SQLException {

        if (genreIds.isEmpty() || mediaTypes.isEmpty()) {
            return List.of();
        }

        List<MediaEntry> results = new ArrayList<>();

        String sql = """
        SELECT DISTINCT
            m.id,
            m.title,
            m.description,
            m.release_year,
            m.age_restriction,
            mt.name AS media_type,
            u.username
        FROM media_entries m
        JOIN media_entries_genres meg ON m.id = meg.media_entry_id
        JOIN media_types mt ON m.media_type = mt.id
        JOIN users u ON m.created_by = u.id
        WHERE meg.genre_id IN (%s)
          OR m.media_type IN (%s)
          OR m.age_restriction <= ?
        ORDER BY m.title
        """.formatted(
                buildPlaceholders(genreIds.size()),
                buildPlaceholders(mediaTypes.size())
        );

        try (PreparedStatement ps = uow.prepareStatement(sql)) {

            int index = 1;

            // Bind genre IDs
            for (Integer genreId : genreIds) {
                ps.setInt(index++, genreId);
            }

            // Bind media types
            for (Integer mediaType : mediaTypes) {
                ps.setInt(index++, mediaType);
            }

            // Bind age restriction
            ps.setInt(index++, maxAgeRestriction);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    long mediaId = rs.getLong("id");

                    if (excludedMediaIds.contains(mediaId)) {
                        continue;
                    }

                    String createdBy = rs.getString("username");

                    String[] genres = loadGenresForMedia(uow, mediaId);

                    results.add(new MediaEntry(
                            createdBy,
                            rs.getString("title"),
                            rs.getString("description"),
                            MediaEntry.stringToMediaType(rs.getString("media_type")),
                            rs.getInt("release_year"),
                            genres,
                            rs.getInt("age_restriction")
                    ));
                }
            }
        }

        return results;
    }

    private String buildPlaceholders(int count) {
        return String.join(",", Collections.nCopies(count, "?"));
    }

    @Override
    public Map<String, Double> getLeaderboard() {
        UnitOfWork uow = new UnitOfWork();
        Map<String, Double> avgRatingsByUsers = new HashMap<>();

        try {
            String sql = """
            SELECT
                u.username,
                AVG(r.star_value) AS avg_rating
            FROM users u
            LEFT JOIN media_entries m
                ON m.created_by = u.id
            LEFT JOIN ratings r
                ON r.media_entry_id = m.id
            GROUP BY
                u.username
            ORDER BY avg_rating DESC NULLS LAST
        """;

            try (PreparedStatement ps = uow.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    String username = rs.getString("username");
                    Double averageRating = rs.getDouble("avg_rating");

                    if (rs.wasNull()) {
                        averageRating = null;
                    }

                    avgRatingsByUsers.put(username, averageRating);
                }
            }

            uow.commitTransaction();
            return avgRatingsByUsers;

        } catch (Exception e) {
            uow.rollbackTransaction();
            throw new DataAccessException(
                    "Failed to fetch users ordered by average media rating",
                    e
            );
        }
    }
}
