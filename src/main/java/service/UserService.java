package service;

import model.User;
import repository.DataAccessException;
import repository.repository.IUserRepository;
import restserver.http.ContentType;
import restserver.http.HttpStatus;
import restserver.server.Response;
import com.fasterxml.jackson.core.JsonProcessingException;

public class UserService extends ICanMapObjects implements IUserService {
    private static UserService instance = null;
    private IUserRepository userRepository;

    private UserService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public static UserService getInstance(IUserRepository userRepository) {
        if (instance == null) {
            instance = new UserService(userRepository);
        }
        return instance;
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
            String userJSON = this.getObjectMapper().writeValueAsString(user);

            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    userJSON
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
            String userJSON = this.getObjectMapper().writeValueAsString(user.getRatings());

            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    userJSON
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
            String userJSON = this.getObjectMapper().writeValueAsString(user.getFavorites());

            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    userJSON
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
            User user = this.getObjectMapper().readValue(requestBody, User.class);
            if (userRepository.get(user.getUsername()) != null) {
                return new Response(
                        HttpStatus.BAD_REQUEST,
                        ContentType.JSON,
                        "{ message: \"User with username exists already! \" }"
                );
            }

            userRepository.add(user);

            return new Response(
                    HttpStatus.CREATED,
                    ContentType.JSON,
                    "{ message: \"Successfully registered user " + user.getUsername() + "\" }"
            );
        } catch (JsonProcessingException e) {
            e.printStackTrace();

            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"message\" : \"Internal Server Error\" }"
            );
        } catch (DataAccessException e) {
            e.printStackTrace();

            return new Response(
                    HttpStatus.BAD_REQUEST,
                    ContentType.JSON,
                    "{ \"message\" : \"" + e.getMessage() + "\" }"
            );
        }
    }

    // POST /users/login
    @Override
    public Response login(String requestBody)
    {
        try {
            User user = this.getObjectMapper().readValue(requestBody, User.class);
            String username = user.getUsername();
            User existingUser = userRepository.get(username);

            if (existingUser == null || !existingUser.getPassword().equals(user.getPassword())) {
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
        User existingUser = getUser(id);

        if (existingUser == null) {
            return new Response(
                    HttpStatus.NOT_FOUND,
                    ContentType.JSON,
                    "{ \"message\" : \"User with id " + id + " not found.\" }"
            );
        }

        try {
            User updatedUser = this.getObjectMapper().readValue(requestBody, User.class);

            this.getObjectMapper().updateValue(existingUser, updatedUser);

            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    "{ \"message\" : \"User with id " + id + " has been updated.\" }"
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
