package controller;

import model.User;
import model.UserProfile;
import repository.repository.IUserRepository;
import restserver.http.ContentType;
import restserver.http.HttpStatus;
import restserver.server.Response;
import repository.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import service.AuthenticationService;

public class UserController extends Controller implements IUserController {
    private IUserRepository userRepository;

    public UserController(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // GET /users/:id/profile
    @Override
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
    @Override
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

    @Override
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

    @Override
    public Response getRecommendationsByGenre(String id) {
        // TODO
        System.out.println("getRecommendationsByGenre");
        return null;
    }

    @Override
    public Response getRecommendationsByContent(String id) {
        // TODO
        System.out.println("getRecommendationsByGenre");
        return null;
    }

    @Override
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
    @Override
    public Response register(String requestBody)
    {
        try {
            UserProfile userProfile = this.getObjectMapper().readValue(requestBody, UserProfile.class);
            User user = new User(userProfile);
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
    @Override
    public Response login(String requestBody)
    {
        try {
            UserProfile userProfile = this.getObjectMapper().readValue(requestBody, UserProfile.class);
            User user = new User(userProfile);
            String username = user.getUserProfile().getUsername();
            User existingUser = userRepository.get(username);

            if (existingUser == null || !existingUser.getUserProfile().getPassword().equals(user.getUserProfile().getPassword())) {
                return new Response(
                        HttpStatus.NOT_FOUND,
                        ContentType.JSON,
                        "{ \"message\" : \"Invalid credentials!\" }"
                );
            }

            return new Response(
                    HttpStatus.CREATED,
                    ContentType.JSON,
                    AuthenticationService.getInstance().generateToken()
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

    // PUT /users/id:/profile
    @Override
    public Response updateProfile(String id, String requestBody)
    {
        User user = getUser(id);

        if (user == null) {
            return new Response(
                    HttpStatus.NOT_FOUND,
                    ContentType.JSON,
                    "{ \"message\" : \"User with id " + id + " not found.\" }"
            );
        }

        try {
            UserProfile userProfile = this.getObjectMapper().readValue(requestBody, UserProfile.class);

            this.getObjectMapper().updateValue(user.getUserProfile(), userProfile);

            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    "{ \"message\" : \"Profile of user with id " + id + " has been updated.\" }"
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
}
