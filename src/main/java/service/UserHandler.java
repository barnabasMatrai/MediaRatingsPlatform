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
            Response response = null;

            String requestMethod = httpExchange.getRequestMethod();
            List<String> pathParts = request.getPathParts();

            if (requestMethod.equals(Method.GET.name())) {
                if (pathParts.size() == 3) {
                    String requestedData = request.getPathParts().get(2);
                    if (requestedData.equals("profile")) {
                        response = this.userController.getProfile(request.getPathParts().get(1));
                    } else if (requestedData.equals("ratings")) {
                        response = this.userController.getRatings(request.getPathParts().get(1));
                    } else if (requestedData.equals("favorites")) {
                        response = this.userController.getFavorites(request.getPathParts().get(1));
                    }
                } else {
                    //response = this.userController.getWeather();
                }
            } else if (requestMethod.equals(Method.POST.name())) {
                if (pathParts.size() == 2) {
                    if (pathParts.get(1).equals("register")) {
                        response = this.userController.register(IOUtils.toString(httpExchange.getRequestBody(), StandardCharsets.UTF_8));
                    }
                    else if (pathParts.get(1).equals("login")) {
                        response = this.userController.login(IOUtils.toString(httpExchange.getRequestBody(), StandardCharsets.UTF_8));
                    }
                }
                //response = this.userController.addWeather(IOUtils.toString(httpExchange.getRequestBody(), StandardCharsets.UTF_8));
            } else if (requestMethod.equals(Method.PUT.name())) {
                if (pathParts.size() == 3) {
                    String requestedData = request.getPathParts().get(2);
                    if (requestedData.equals("profile")) {
//                        response = this.userController.updateProfile();
                    }
                }

            } else if (requestMethod.equals(Method.DELETE.name())) {

            } else {
                response = new Response(
                        HttpStatus.BAD_REQUEST,
                        ContentType.JSON,
                        "[]"
                );
            }

            response.send(httpExchange);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
