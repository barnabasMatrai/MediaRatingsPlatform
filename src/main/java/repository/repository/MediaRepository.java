package repository.repository;

import model.MediaEntry;
import model.Rating;
import repository.DataAccessException;
import repository.ForbiddenException;
import repository.UnitOfWork;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MediaRepository implements IMediaRepository {
    private static MediaRepository instance = null;

    private MediaRepository() {}

    public static MediaRepository getInstance() {
        if (instance == null) {
            instance = new MediaRepository();
        }

        return instance;
    }

    public long getUserIdByUsername(String username) {
        UnitOfWork uow = new UnitOfWork();

        try {
            try (PreparedStatement ps =
                         uow.prepareStatement(
                                 "SELECT id FROM users WHERE username = ?"
                         )) {

                ps.setString(1, username);

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException("User not found: " + username);
                    }

                    long userId = rs.getLong("id");
                    uow.commitTransaction();
                    return userId;
                }
            }
        } catch (SQLException e) {
            uow.rollbackTransaction();
            throw new DataAccessException("Failed to get user ID", e);
        }
    }

    public int getMediaTypeId(String mediaType) {
        UnitOfWork uow = new UnitOfWork();

        try {
            try (PreparedStatement ps =
                         uow.prepareStatement(
                                 "SELECT id FROM media_types WHERE name = ?"
                         )) {

                ps.setString(1, mediaType.toLowerCase());

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException("Unknown media type: " + mediaType);
                    }

                    int id = rs.getInt("id");
                    uow.commitTransaction();
                    return id;
                }
            }
        } catch (SQLException e) {
            uow.rollbackTransaction();
            throw new DataAccessException("Failed to get media type ID", e);
        }
    }

    public int getOrCreateGenreId(String genreName) {
        UnitOfWork uow = new UnitOfWork();

        try {
            // Try existing
            try (PreparedStatement ps =
                         uow.prepareStatement(
                                 "SELECT id FROM genres WHERE name = ?"
                         )) {

                ps.setString(1, genreName);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int id = rs.getInt("id");
                        uow.commitTransaction();
                        return id;
                    }
                }
            }

            // Insert new
            try (PreparedStatement ps =
                         uow.prepareStatement(
                                 "INSERT INTO genres (name) VALUES (?)"
                         )) {

                ps.setString(1, genreName);
                ps.executeUpdate();
            }

            // Get ID
            try (PreparedStatement ps =
                         uow.prepareStatement(
                                 "SELECT currval('genres_id_seq')"
                         );
                 ResultSet rs = ps.executeQuery()) {

                rs.next();
                int id = rs.getInt(1);
                uow.commitTransaction();
                return id;
            }

        } catch (SQLException e) {
            uow.rollbackTransaction();
            throw new DataAccessException("Failed to get or create genre", e);
        }
    }

    @Override
    public MediaEntry get(long id) {
        UnitOfWork uow = new UnitOfWork();

        try {
            String title;
            String description;
            int releaseYear;
            int ageRestriction;
            String mediaType;
            String createdBy;

            // 1️⃣ Load main media entry + full user + media type
            try (PreparedStatement ps =
                         uow.prepareStatement(
                                 """
                                 SELECT
                                     m.title,
                                     m.description,
                                     m.release_year,
                                     m.age_restriction,
                                     mt.name AS media_type,
                                     u.username
                                 FROM media_entries m
                                 JOIN users u ON m.created_by = u.id
                                 JOIN media_types mt ON m.media_type = mt.id
                                 WHERE m.id = ?
                                 """
                         )) {

                ps.setLong(1, id);

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        uow.commitTransaction();
                        return null;
                    }

                    title = rs.getString("title");
                    description = rs.getString("description");
                    releaseYear = rs.getInt("release_year");
                    ageRestriction = rs.getInt("age_restriction");
                    mediaType = rs.getString("media_type");
                    createdBy = rs.getString("username");
                }
            }

            // 2️⃣ Load genres
            String[] genres;
            try (PreparedStatement ps =
                         uow.prepareStatement(
                                 """
                                 SELECT g.name
                                 FROM genres g
                                 JOIN media_entries_genres meg
                                   ON g.id = meg.genre_id
                                 WHERE meg.media_entry_id = ?
                                 ORDER BY g.name
                                 """
                         )) {

                ps.setLong(1, id);

                try (ResultSet rs = ps.executeQuery()) {
                    List<String> genreList = new ArrayList<>();
                    while (rs.next()) {
                        genreList.add(rs.getString("name"));
                    }
                    genres = genreList.toArray(new String[0]);
                }
            }

            uow.commitTransaction();

            return new MediaEntry(
                    createdBy,
                    title,
                    description,
                    MediaEntry.stringToMediaType(mediaType),
                    releaseYear,
                    genres,
                    ageRestriction
            );

        } catch (Exception e) {
            uow.rollbackTransaction();
            throw new DataAccessException("Failed to fetch media entry", e);
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
    public List<MediaEntry> get(Map<String, String> filters) {
        UnitOfWork uow = new UnitOfWork();
        List<MediaEntry> results = new ArrayList<>();

        try {
            StringBuilder sql = new StringBuilder("""
            SELECT
                m.id,
                m.title,
                m.description,
                m.release_year,
                m.age_restriction,
                mt.name AS media_type,
                u.username,
                COALESCE(AVG(r.star_value), 0) AS avg_rating
            FROM media_entries m
            JOIN users u ON m.created_by = u.id
            JOIN media_types mt ON m.media_type = mt.id
            LEFT JOIN media_entries_genres meg ON m.id = meg.media_entry_id
            LEFT JOIN genres g ON meg.genre_id = g.id
            LEFT JOIN ratings r ON r.media_entry_id = m.id
            WHERE 1 = 1
        """);

            List<Object> params = new ArrayList<>();

            // FILTERS

            if (filters.containsKey("title")) {
                sql.append(" AND LOWER(m.title) LIKE ?");
                params.add("%" + filters.get("title").toLowerCase() + "%");
            }

            if (filters.containsKey("genre")) {
                sql.append(" AND LOWER(g.name) = ?");
                params.add(filters.get("genre").toLowerCase());
            }

            if (filters.containsKey("mediaType")) {
                sql.append(" AND mt.name = ?");
                params.add(filters.get("mediaType"));
            }

            if (filters.containsKey("releaseYear")) {
                sql.append(" AND m.release_year = ?");
                params.add(Integer.parseInt(filters.get("releaseYear")));
            }

            if (filters.containsKey("ageRestriction")) {
                sql.append(" AND m.age_restriction <= ?");
                params.add(Integer.parseInt(filters.get("ageRestriction")));
            }

            // GROUP BY

            sql.append("""
                GROUP BY
                    m.id,
                    m.title,
                    m.description,
                    m.release_year,
                    m.age_restriction,
                    mt.name,
                    u.username
            """);

            // RATING FILTER

            if (filters.containsKey("rating")) {
                sql.append(" HAVING COALESCE(AVG(r.star_value), 0) >= ?");
                params.add(Double.parseDouble(filters.get("rating")));
            }

            // SORTING

            if (filters.containsKey("sortBy")) {
                switch (filters.get("sortBy")) {
                    case "title" -> sql.append(" ORDER BY m.title");
                    case "releaseYear" -> sql.append(" ORDER BY m.release_year DESC");
                    case "score" -> sql.append(" ORDER BY avg_rating DESC");
                    default -> sql.append(" ORDER BY m.title");
                }
            } else {
                sql.append(" ORDER BY m.title");
            }

            // EXECUTE

            try (PreparedStatement ps = uow.prepareStatement(sql.toString())) {
                for (int i = 0; i < params.size(); i++) {
                    ps.setObject(i + 1, params.get(i));
                }

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
            throw new DataAccessException("Failed to fetch media entries", e);
        }
    }

    @Override
    public void add(MediaEntry mediaEntry, String username) {
        UnitOfWork uow = new UnitOfWork();

        try {
            Long userId = null;
            if (username != null) {
                userId = getUserIdByUsername(username);
            }

            int mediaTypeId = getMediaTypeId(mediaEntry.getMediaTypeAsString());

            try (PreparedStatement ps =
                         uow.prepareStatement(
                                 """
                                 INSERT INTO media_entries
                                 (created_by, title, description, media_type, release_year, age_restriction)
                                 VALUES (?, ?, ?, ?, ?, ?)
                                 """
                         )) {

                ps.setLong(1, userId);
                ps.setString(2, mediaEntry.getTitle());
                ps.setString(3, mediaEntry.getDescription());
                ps.setInt(4, mediaTypeId);
                ps.setInt(5, mediaEntry.getReleaseYear());
                ps.setInt(6, mediaEntry.getAgeRestriction());
                ps.executeUpdate();
            }

            long mediaEntryId;
            try (PreparedStatement ps =
                         uow.prepareStatement(
                                 "SELECT currval('media_entries_id_seq')"
                         );
                 ResultSet rs = ps.executeQuery()) {

                rs.next();
                mediaEntryId = rs.getLong(1);
            }

            try (PreparedStatement ps =
                         uow.prepareStatement(
                                 """
                                 INSERT INTO media_entries_genres
                                 (media_entry_id, genre_id)
                                 VALUES (?, ?)
                                 """
                         )) {

                for (String genre : mediaEntry.getGenres()) {
                    int genreId = getOrCreateGenreId(genre);
                    ps.setLong(1, mediaEntryId);
                    ps.setInt(2, genreId);
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            uow.commitTransaction();

        } catch (Exception e) {
            uow.rollbackTransaction();
            throw new DataAccessException("Failed to create media entry", e);
        }
    }

    @Override
    public void delete(long mediaEntryId, long userId) {
        UnitOfWork uow = new UnitOfWork();

        try {
            try (PreparedStatement ps =
                         uow.prepareStatement(
                                 """
                                 DELETE FROM media_entries
                                 WHERE id = ? AND created_by = ?
                                 """
                         )) {

                ps.setLong(1, mediaEntryId);
                ps.setLong(2, userId);
                int rows = ps.executeUpdate();

                if (rows == 0) {
                    throw new ForbiddenException("Not allowed to delete this media entry");
                }
            }

            uow.commitTransaction();
        } catch (ForbiddenException e) {
            uow.rollbackTransaction();
            throw e;
        } catch (Exception e) {
            uow.rollbackTransaction();
            throw new DataAccessException("Failed to delete media entry", e);
        }
    }

    @Override
    public void update(MediaEntry updatedMediaEntry, long mediaEntryId, long userId) {
        UnitOfWork uow = new UnitOfWork();

        try {
            try (PreparedStatement ps =
                         uow.prepareStatement(
                                 """
                                 UPDATE media_entries
                                 SET
                                     title = ?,
                                     description = ?,
                                     media_type = (
                                         SELECT id FROM media_types WHERE name = ?
                                     ),
                                     release_year = ?,
                                     age_restriction = ?
                                 WHERE id = ? AND created_by = ?
                                 """
                         )) {

                ps.setString(1, updatedMediaEntry.getTitle());
                ps.setString(2, updatedMediaEntry.getDescription());
                ps.setString(3, updatedMediaEntry.getMediaType().name().toLowerCase());
                ps.setInt(4, updatedMediaEntry.getReleaseYear());
                ps.setInt(5, updatedMediaEntry.getAgeRestriction());
                ps.setLong(6, mediaEntryId);
                ps.setLong(7, userId);

                int rows = ps.executeUpdate();
                if (rows == 0) {
                    throw new ForbiddenException("Not allowed to update this media entry");
                }
            }

            try (PreparedStatement ps =
                         uow.prepareStatement(
                                 """
                                 DELETE FROM media_entries_genres
                                 WHERE media_entry_id = ?
                                 """
                         )) {

                ps.setLong(1, mediaEntryId);
                ps.executeUpdate();
            }

            try (PreparedStatement ps =
                         uow.prepareStatement(
                                 """
                                 INSERT INTO media_entries_genres (media_entry_id, genre_id)
                                 VALUES (?,?)
                                 """
                         )) {

                for (String genre : updatedMediaEntry.getGenres()) {
                    int genreId = getOrCreateGenreId(genre);
                    ps.setLong(1, mediaEntryId);
                    ps.setLong(2, genreId);
                    ps.addBatch();
                }

                ps.executeBatch();
            }

            uow.commitTransaction();

        } catch (DataAccessException e) {
            uow.rollbackTransaction();
            throw e;
        } catch (Exception e) {
            uow.rollbackTransaction();
            throw new DataAccessException("Failed to update media entry", e);
        }
    }

    @Override
    public void rate(Rating rating, long mediaEntryId, long userId) {
        UnitOfWork uow = new UnitOfWork();

        try {
            try (PreparedStatement checkPs =
                         uow.prepareStatement(
                                 """
                                 SELECT 1
                                 FROM ratings
                                 WHERE created_by = ? AND media_entry_id = ?
                                 """
                         )) {

                checkPs.setLong(1, userId);
                checkPs.setLong(2, mediaEntryId);

                try (ResultSet rs = checkPs.executeQuery()) {
                    if (rs.next()) {
                        throw new DataAccessException(
                                "User has already rated this media entry"
                        );
                    }
                }
            }

            try (PreparedStatement insertPs =
                         uow.prepareStatement(
                                 """
                                 INSERT INTO ratings (
                                     created_by,
                                     media_entry_id,
                                     star_value,
                                     comment,
                                     is_confirmed
                                 )
                                 VALUES (?, ?, ?, ?, ?)
                                 """
                         )) {

                boolean isConfirmed = Boolean.TRUE.equals(rating.getIsConfirmed());
                insertPs.setLong(1, userId);
                insertPs.setLong(2, mediaEntryId);
                insertPs.setShort(3, rating.getStars()); // 1–5
                insertPs.setString(4, rating.getComment());
                insertPs.setBoolean(5, isConfirmed);

                insertPs.executeUpdate();
            }

            uow.commitTransaction();

        } catch (DataAccessException e) {
            uow.rollbackTransaction();
            throw e;
        } catch (Exception e) {
            uow.rollbackTransaction();
            throw new DataAccessException("Failed to rate media entry", e);
        }
    }

    @Override
    public void favorite(long mediaEntryId, long userId) {
        UnitOfWork uow = new UnitOfWork();

        try {
            try (PreparedStatement ps =
                         uow.prepareStatement(
                                 """
                                 SELECT 1
                                 FROM user_favorites
                                 WHERE user_id = ? AND media_entry_id = ?
                                 """
                         )) {

                ps.setLong(1, userId);
                ps.setLong(2, mediaEntryId);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        throw new DataAccessException("You have already marked this media entry as favorite");
                    }
                }
            }

            try (PreparedStatement ps =
                         uow.prepareStatement(
                                 """
                                 INSERT INTO user_favorites (user_id, media_entry_id)
                                 VALUES (?, ?)
                                 """
                         )) {

                ps.setLong(1, userId);
                ps.setLong(2, mediaEntryId);
                ps.executeUpdate();
            }

            uow.commitTransaction();

        } catch (DataAccessException e) {
            uow.rollbackTransaction();
            throw e;
        } catch (Exception e) {
            uow.rollbackTransaction();
            throw new DataAccessException("Failed to mark media as favorite", e);
        }
    }

    @Override
    public void unfavorite(long mediaEntryId, long userId) {
        UnitOfWork uow = new UnitOfWork();

        try {
            try (PreparedStatement ps =
                         uow.prepareStatement(
                                 """
                                 DELETE FROM user_favorites
                                 WHERE user_id = ? AND media_entry_id = ?
                                 """
                         )) {

                ps.setLong(1, userId);
                ps.setLong(2, mediaEntryId);

                int rows = ps.executeUpdate();
                if (rows == 0) {
                    throw new DataAccessException(
                            "Media entry is not marked as favorite"
                    );
                }
            }

            uow.commitTransaction();

        } catch (DataAccessException e) {
            uow.rollbackTransaction();
            throw e;
        } catch (Exception e) {
            uow.rollbackTransaction();
            throw new DataAccessException("Failed to unfavorite media entry", e);
        }
    }
}
