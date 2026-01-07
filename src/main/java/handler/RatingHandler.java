package handler;

import com.sun.net.httpserver.HttpExchange;
import restserver.http.ContentType;
import restserver.http.HttpStatus;
import restserver.server.Request;
import restserver.server.Response;
import service.AuthenticationService;
import service.IMediaService;
import service.IRatingService;

import java.util.List;
import java.util.Map;

public class RatingHandler extends Handler {
    private final IRatingService ratingService;

    public RatingHandler(IRatingService ratingService) {
        this.ratingService = ratingService;
    }

    @Override
    protected Response handleGet(List<String> path, HttpExchange exchange, Map<String, String> params) {
        return badRequest();
    }

    @Override
    protected Response handlePost(List<String> path, HttpExchange exchange, String body) {
        String username = AuthenticationService.getInstance().getCurrentUser(exchange);

        if (username == null) {
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "User is not logged in, unable to continue with request");
        }

        if (path.size() == 4) {
            String ratingId = path.get(2);
            String option = path.get(3);

            switch (option) {
                case "like":
                    return ratingService.likeRating(ratingId, username);
                case "confirm":
                    return ratingService.confirmRating(ratingId, username);
            }
        }

        return badRequest();
    }

    @Override
    protected Response handlePut(List<String> path, HttpExchange exchange, String body) {
        String username = AuthenticationService.getInstance().getCurrentUser(exchange);
        if (username == null) {
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "User is not logged in, unable to continue with request");
        }
        if (path.size() < 4) {
            String ratingId = path.get(2);
            return ratingService.updateRating(body, ratingId, username);
        }

        return badRequest();
    }

    @Override
    protected Response handleDelete(List<String> path, HttpExchange exchange) {
        return badRequest(); // placeholder
    }
}
