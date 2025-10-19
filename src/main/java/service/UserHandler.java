package service;

import controller.UserController;
import restserver.http.ContentType;
import restserver.http.HttpStatus;
import restserver.http.Method;
import restserver.server.Request;
import restserver.server.Response;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class UserHandler implements HttpHandler {
    private final UserController userController;

    public UserHandler() {
        this.userController = new UserController();
    }

    @Override
    public void handle(HttpExchange httpExchange) {
        try {
            Request request = new Request(httpExchange.getRequestURI());
            String method = httpExchange.getRequestMethod();
            List<String> path = request.getPathParts();

            Response response = routeRequest(method, path, httpExchange, request);
            response.send(httpExchange);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Response routeRequest(String method, List<String> path, HttpExchange exchange, Request request) throws IOException {
        String body = IOUtils.toString(exchange.getRequestBody(), StandardCharsets.UTF_8);

        if (method.equals(Method.GET.name())) {
            return handleGet(path, request);
        } else if (method.equals(Method.POST.name())) {
            return handlePost(path, body);
        } else if (method.equals(Method.PUT.name())) {
            return handlePut(path, body);
        }  else if (method.equals(Method.DELETE.name())) {
            return handleDelete(path);
        }

        return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "[]");
    }

    private Response handleGet(List<String> path, Request request) {
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

    private Response handlePost(List<String> path, String body) {
        if (path.size() < 3) return badRequest();

        return switch (path.get(2)) {
            case "register" -> userController.register(body);
            case "login" -> userController.login(body);
            default -> badRequest();
        };
    }

    private Response handlePut(List<String> path, String body) {
        if (path.size() < 4) return badRequest();

        String userId = path.get(2);
        String target = path.get(3);

        return switch (target) {
            case "profile" -> userController.updateProfile(userId, body);
            default -> badRequest();
        };
    }

    private Response handleDelete(List<String> path) {
        return badRequest(); // placeholder
    }

    private Response badRequest() {
        return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "[]");
    }
}
