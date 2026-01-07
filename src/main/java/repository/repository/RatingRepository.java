package repository.repository;

import model.Rating;
import repository.DataAccessException;
import repository.ForbiddenException;
import repository.UnitOfWork;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RatingRepository implements IRatingRepository {
    private static IRatingRepository instance = null;

    private RatingRepository() {}

    public static IRatingRepository getInstance() {
        if (instance == null) {
            instance = new RatingRepository();
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

    @Override
    public void likeRating(long ratingId, long userId) {
        UnitOfWork uow = new UnitOfWork();

        try {
            try (PreparedStatement ps =
                         uow.prepareStatement(
                                 """
                                 SELECT 1
                                 FROM user_liked_ratings
                                 WHERE user_id = ? AND rating_id = ?
                                 """
                         )) {

                ps.setLong(1, userId);
                ps.setLong(2, ratingId);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        throw new DataAccessException("You have already liked this rating");
                    }
                }
            }

            try (PreparedStatement ps =
                         uow.prepareStatement(
                                 """
                                 INSERT INTO user_liked_ratings (user_id, rating_id)
                                 VALUES (?, ?)
                                 """
                         )) {

                ps.setLong(1, userId);
                ps.setLong(2, ratingId);
                ps.executeUpdate();
            }

            uow.commitTransaction();

        } catch (DataAccessException e) {
            uow.rollbackTransaction();
            throw e;
        } catch (Exception e) {
            uow.rollbackTransaction();
            throw new DataAccessException("Failed to like rating", e);
        }
    }

    @Override
    public void update(Rating updatedRating, long ratingId, long userId) {
        UnitOfWork uow = new UnitOfWork();

        try {
            StringBuilder sql = new StringBuilder("""
            UPDATE ratings
            SET
        """);

            List<Object> params = new ArrayList<>();
            boolean first = true;

            if (updatedRating.getStars() != null) {
                sql.append(first ? "" : ", ")
                        .append("star_value = ?");
                params.add(updatedRating.getStars());
                first = false;
            }

            if (updatedRating.getComment() != null) {
                sql.append(first ? "" : ", ")
                        .append("comment = ?");
                params.add(updatedRating.getComment());
                first = false;
            }

            if (updatedRating.getIsConfirmed() != null) {
                sql.append(first ? "" : ", ")
                        .append("is_confirmed = ?");
                params.add(updatedRating.getIsConfirmed());
                first = false;
            }

            if (params.isEmpty()) {
                throw new DataAccessException("No fields provided to update");
            }

            sql.append("""
            WHERE id = ? AND created_by = ?
        """);

            params.add(ratingId);
            params.add(userId);

            try (PreparedStatement ps = uow.prepareStatement(sql.toString())) {
                for (int i = 0; i < params.size(); i++) {
                    ps.setObject(i + 1, params.get(i));
                }

                int rows = ps.executeUpdate();
                if (rows == 0) {
                    throw new ForbiddenException("Not allowed to update this rating");
                }
            }

            uow.commitTransaction();

        } catch (DataAccessException e) {
            uow.rollbackTransaction();
            throw e;
        } catch (ForbiddenException e) {
            uow.rollbackTransaction();
            throw e;
        } catch (Exception e) {
            uow.rollbackTransaction();
            throw new DataAccessException("Failed to update rating", e);
        }
    }

    @Override
    public void confirm(long ratingId, long userId) {
        UnitOfWork uow = new UnitOfWork();

        try {
            try (PreparedStatement ps =
                         uow.prepareStatement(
                                 """
                                 UPDATE ratings
                                 SET is_confirmed = true
                                 WHERE id = ?
                                   AND created_by = ?
                                   AND is_confirmed = false
                                 """
                         )) {

                ps.setLong(1, ratingId);
                ps.setLong(2, userId);

                int rows = ps.executeUpdate();
                if (rows == 0) {
                    throw new DataAccessException(
                            "Rating is already confirmed or you are not allowed to confirm it"
                    );
                }
            }

            uow.commitTransaction();

        } catch (DataAccessException e) {
            uow.rollbackTransaction();
            throw e;
        } catch (Exception e) {
            uow.rollbackTransaction();
            throw new DataAccessException("Failed to confirm rating", e);
        }
    }
}
