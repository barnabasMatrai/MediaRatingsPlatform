package controller;

import jdk.jshell.spi.ExecutionControl;
import model.User;
import restserver.http.ContentType;
import restserver.http.HttpStatus;
import restserver.server.Response;
import repository.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Collection;
import java.util.List;

public class UserController extends Controller {
    private UserRepository userRepository;

    public UserController() {

        this.userRepository = new UserRepository();
    }

    // GET /users/:id/profile
    public Response getProfile(String id) {
        User user = getUser(id);

        if (user == null) {
            return new Response(
                    HttpStatus.NOT_FOUND,
                    ContentType.JSON,
                    "{ \"message\" : \"User with id " + id + " not found.\" }"
            );
        }

        try {
            String userProfileJSON = this.getObjectMapper().writeValueAsString(user.getUserProfile());

            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    userProfileJSON
            );
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"message\" : \"Internal Server Error\" }"
            );
        }
    }

    // GET /users/:id/profile
    public Response getRatings(String id) {
        User user = getUser(id);

        if (user == null) {
            return new Response(
                    HttpStatus.NOT_FOUND,
                    ContentType.JSON,
                    "{ \"message\" : \"User with id " + id + " not found.\" }"
            );
        }

        try {
            String userProfileJSON = this.getObjectMapper().writeValueAsString(user.getRatings());

            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    userProfileJSON
            );
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"message\" : \"Internal Server Error\" }"
            );
        }
    }

    public Response getFavorites(String id) {
        User user = getUser(id);

        if (user == null) {
            return new Response(
                    HttpStatus.NOT_FOUND,
                    ContentType.JSON,
                    "{ \"message\" : \"User with id " + id + " not found.\" }"
            );
        }

        try {
            String userProfileJSON = this.getObjectMapper().writeValueAsString(user.getFavorites());

            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    userProfileJSON
            );
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"message\" : \"Internal Server Error\" }"
            );
        }
    }

    public User getUser(String id) {
        long parsedId;

        try {
            parsedId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        }

        return userRepository.get(parsedId);
    }

    // POST /users/register
    public Response register(String requestBody)
    {
        try {
            User user = this.getObjectMapper().readValue(requestBody, User.class);
            userRepository.add(user);

            return new Response(
                    HttpStatus.CREATED,
                    ContentType.JSON,
                    "{ message: \"Success\" }"
            );
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return new Response(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ContentType.JSON,
                "{ \"message\" : \"Internal Server Error\" }"
        );
    }

    // POST /users/login
    public Response login(String requestBody)
    {
        try {
            User user = this.getObjectMapper().readValue(requestBody, User.class);
            // add login logic
            // this.userRepository.addUser(user);

            return new Response(
                    HttpStatus.CREATED,
                    ContentType.JSON,
                    "{ message: \"Success\" }"
            );
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return new Response(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ContentType.JSON,
                "{ \"message\" : \"Internal Server Error\" }"
        );
    }
}
