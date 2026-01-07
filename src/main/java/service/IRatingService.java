package service;

import restserver.server.Response;

public interface IRatingService {
    Response likeRating(String id, String username);
    Response updateRating(String requestBody, String ratingId, String username);
    Response confirmRating(String ratingId, String username);
}
