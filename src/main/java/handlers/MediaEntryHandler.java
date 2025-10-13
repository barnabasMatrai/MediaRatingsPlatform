package handlers;

import com.sun.net.httpserver.HttpExchange;
import model.MediaEntry;
import services.IMediaEntryService;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

public class MediaEntryHandler extends Handler {
    private IMediaEntryService mediaEntryService;

    public MediaEntryHandler(IMediaEntryService mediaEntryService) {
        this.mediaEntryService = mediaEntryService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod().toUpperCase();
        URI requestURI = exchange.getRequestURI();
        Map<String, String> params = queryToMap(requestURI.getQuery());

        switch (method) {
            case "GET":
                // Get users
//                Gson gson = new Gson();
//                String response = gson.toJson(mediaEntryService.getAll());
//                sendResponse(exchange, response);
                break;
            case "POST":
//                MediaEntry mediaEntry = new MediaEntry();
//                mediaEntryService.add();
                if (params.containsKey("username") && params.containsKey("password")) {
                    // Register/login user
                }
                break;
            case "PUT":
                if (params.containsKey("username") && params.containsKey("password") &&
                        params.containsKey("email") && params.containsKey("firstname") &&
                        params.containsKey("lastname")) {
                    // Update user profile
                }
                break;
            case "DELETE":
                if (params.containsKey("username") && params.containsKey("password")) {
                    // Delete user
                }
                break;
        }
    }
}
