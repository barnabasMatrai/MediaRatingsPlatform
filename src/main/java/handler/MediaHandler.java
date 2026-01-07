package handler;

import com.sun.net.httpserver.HttpExchange;
import restserver.http.ContentType;
import restserver.http.HttpStatus;
import restserver.server.Response;
import service.AuthenticationService;
import service.IMediaService;

import java.util.List;
import java.util.Map;

public class MediaHandler extends Handler {
    private final IMediaService mediaService;

    public MediaHandler(IMediaService mediaService) {
        this.mediaService = mediaService;
    }

    @Override
    protected Response handleGet(List<String> path, HttpExchange exchange, Map<String, String> params) {
        if (path.size() < 3) {
            if (params == null) {
                return badRequest();
            }

            return mediaService.getMediaEntries(params);
        }

        String id = path.get(2);
        return mediaService.getMediaEntry(id);
    }

    @Override
    protected Response handlePost(List<String> path, HttpExchange exchange, String body) {
        String username = AuthenticationService.getInstance().getCurrentUser(exchange);

        if (username == null) {
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "User is not logged in, unable to continue with request");
        }

        if (path.size() == 4) {
            long mediaEntryId = Long.parseLong(path.get(2));
            String option = path.get(3);

            switch (option) {
                case "rate":
                    return mediaService.rateMedia(body, mediaEntryId, username);
                case "favorite":
                    return mediaService.markAsFavorite(mediaEntryId, username);
            }
        }

        if (path.size() < 3) {
            return mediaService.createMedia(body, username);
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
            long mediaEntryId = Long.parseLong(path.get(2));
            return mediaService.updateMedia(body, mediaEntryId, username);
        }

        return badRequest();
    }

    @Override
    protected Response handleDelete(List<String> path, HttpExchange exchange) {
        String username = AuthenticationService.getInstance().getCurrentUser(exchange);

        if (username == null) {
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "User is not logged in, unable to continue with request");
        }

        if (path.size() == 3) {
            return mediaService.deleteMedia(Integer.parseInt(path.get(2)), username);
        } else if (path.size() == 4) {
            long mediaEntryId = Long.parseLong(path.get(2));
            String option = path.get(3);

            switch (option) {
                case "favorite":
                    return mediaService.unmarkAsFavorite(mediaEntryId, username);
            }
        }
        return badRequest(); // placeholder
    }
}
