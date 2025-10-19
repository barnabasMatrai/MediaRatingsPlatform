package service;

import controller.UserController;
import restserver.server.Request;
import restserver.server.Response;

import java.util.List;

public class UserHandler extends Handler {
    private final UserController userController;

    public UserHandler() {
        this.userController = new UserController();
    }

    @Override
    protected Response handleGet(List<String> path, Request request) {
        if (path.size() < 4) return badRequest();

        String userId = path.get(2);
        String target = path.get(3);

        return switch (target) {
            case "profile" -> userController.getProfile(userId);
            case "ratings" -> userController.getRatings(userId);
            case "favorites" -> userController.getFavorites(userId);
            case "recommendations" -> handleRecommendations(request, userId);
            default -> badRequest();
        };
    }

    private Response handleRecommendations(Request request, String userId) {
        String params = request.getParams();
        return switch (params) {
            case "type=genre" -> userController.getRecommendationsByGenre(userId);
            case "type=content" -> userController.getRecommendationsByContent(userId);
            default -> badRequest();
        };
    }

    @Override
    protected Response handlePost(List<String> path, String body) {
        if (path.size() < 3) return badRequest();

        return switch (path.get(2)) {
            case "register" -> userController.register(body);
            case "login" -> userController.login(body);
            default -> badRequest();
        };
    }

    @Override
    protected Response handlePut(List<String> path, String body) {
        if (path.size() < 4) return badRequest();

        String userId = path.get(2);
        String target = path.get(3);

        return switch (target) {
            case "profile" -> userController.updateProfile(userId, body);
            default -> badRequest();
        };
    }

    @Override
    protected Response handleDelete(List<String> path) {
        return badRequest(); // placeholder
    }
}
