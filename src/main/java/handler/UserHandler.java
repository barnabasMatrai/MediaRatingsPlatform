package handler;

import com.sun.net.httpserver.HttpExchange;
import restserver.http.ContentType;
import restserver.http.HttpStatus;
import service.AuthenticationService;
import service.IUserService;
import restserver.server.Request;
import restserver.server.Response;

import java.util.List;
import java.util.Map;

public class UserHandler extends Handler {
    private final IUserService userService;

    public UserHandler(IUserService userService) {
        this.userService = userService;
    }

    @Override
    protected Response handleGet(List<String> path, HttpExchange exchange, Map<String, String> params) {
        if (path.size() < 3) {
            return badRequest();
        } else if (path.size() == 3) {
            String target = path.get(2);

            if (target.equals("leaderboard")) {
                return userService.getLeaderboard();
            }
        }
        String username = AuthenticationService.getInstance().getCurrentUser(exchange);
        if (username == null) {
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "User is not logged in, unable to continue with request");
        }

        String userId = path.get(2);
        String target = path.get(3);

        return switch (target) {
            case "profile" -> userService.getProfile(userId);
            case "ratings" -> userService.getRatings(userId);
            case "favorites" -> userService.getFavorites(userId);
            case "recommendations" -> handleRecommendations(params, userId);
            default -> badRequest();
        };
    }

    private Response handleRecommendations(Map<String, String> params, String userId) {
        return switch (params.get("type")) {
            case "genre" -> userService.getRecommendationsByGenre(userId);
            case "content" -> userService.getRecommendationsByContent(userId);
            default -> badRequest();
        };
    }

    @Override
    protected Response handlePost(List<String> path, HttpExchange exchange, String body) {
        if (path.size() < 3) return badRequest();

        return switch (path.get(2)) {
            case "register" -> userService.register(body);
            case "login" -> userService.login(body);
            default -> badRequest();
        };
    }

    @Override
    protected Response handlePut(List<String> path, HttpExchange exchange, String body) {
        String username = AuthenticationService.getInstance().getCurrentUser(exchange);
        if (username == null) {
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "User is not logged in, unable to continue with request");
        }

        if (path.size() < 4) return badRequest();

        String userId = path.get(2);
        String target = path.get(3);

        return switch (target) {
            case "profile" -> userService.updateProfile(userId, body);
            default -> badRequest();
        };
    }

    @Override
    protected Response handleDelete(List<String> path, HttpExchange exchange) {
        return badRequest(); // placeholder
    }
}
