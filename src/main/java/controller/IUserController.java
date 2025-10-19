package controller;

import model.User;
import restserver.server.Response;

public interface IUserController {
    Response getProfile(String id);
    Response getRatings(String id);
    Response getFavorites(String id);
    Response getRecommendationsByGenre(String id);
    Response getRecommendationsByContent(String id);
    User getUser(String id);
    Response register(String requestBody);
    Response login(String requestBody);
    Response updateProfile(String id, String requestBody);
}
