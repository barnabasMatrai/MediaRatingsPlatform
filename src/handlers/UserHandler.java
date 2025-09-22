package handlers;

import services.IUserService;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

public class UserHandler extends Handler {
    private IUserService userService;

    public UserHandler(IUserService userService) {
        this.userService = userService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod().toUpperCase();
        URI requestURI = exchange.getRequestURI();
        Map<String, String> params = queryToMap(requestURI.getQuery());

        switch (method) {
            case "GET":
                if (params.containsKey("username")) {
                    // Get user data
                } else {
                    // Get users
                    Gson gson = new Gson();
                    String response = gson.toJson(userService.getUsers());
                    sendResponse(exchange, response);
                }
                break;
            case "POST":
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
