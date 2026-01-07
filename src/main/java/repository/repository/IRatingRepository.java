package repository.repository;

import model.Rating;

public interface IRatingRepository {
    long getUserIdByUsername(String username);
    void likeRating(long id, long userId);
    void update(Rating updatedRating, long ratingId, long userId);
    void confirm(long ratingId, long userId);
}
