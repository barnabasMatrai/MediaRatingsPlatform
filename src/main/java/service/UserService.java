package service;

import model.MediaEntry;
import model.User;
import repository.DataAccessException;
import repository.repository.IUserRepository;
import restserver.http.ContentType;
import restserver.http.HttpStatus;
import restserver.server.Response;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;
import java.util.Map;

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
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"message\" : \"Internal Server Error\" }"
            );
        }
    }

    // GET /users/:id/ratings
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
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"message\" : \"Internal Server Error\" }"
            );
        }
    }

    // GET /users/:id/favorites
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
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"message\" : \"Internal Server Error\" }"
            );
        }
    }

    // GET /users/id:/recommendations/?type=genre
    @Override
    public Response getRecommendationsByGenre(String id)
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
            long parsedId = Long.parseLong(id);
            List<MediaEntry> recommendations = userRepository.getRecommendationsByGenre(parsedId);

            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    getObjectMapper().writeValueAsString(recommendations)
            );
        } catch (JsonProcessingException e) {
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"message\" : \"Internal Server Error\" }"
            );
        } catch (DataAccessException e) {
            return new Response(
                    HttpStatus.BAD_REQUEST,
                    ContentType.JSON,
                    "{ \"message\" : \"" + e.getMessage() + "\" }"
            );
        }
    }

    // GET /users/id:/recommendations/?type=content
    @Override
    public Response getRecommendationsByContent(String id) {
        User existingUser = getUser(id);

        if (existingUser == null) {
            return new Response(
                    HttpStatus.NOT_FOUND,
                    ContentType.JSON,
                    "{ \"message\" : \"User with id " + id + " not found.\" }"
            );
        }

        try {
            long parsedId = Long.parseLong(id);
            List<MediaEntry> recommendations = userRepository.getRecommendationsByContent(parsedId);

            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    getObjectMapper().writeValueAsString(recommendations)
            );
        } catch (JsonProcessingException e) {
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"message\" : \"Internal Server Error\" }"
            );
        } catch (DataAccessException e) {
            return new Response(
                    HttpStatus.BAD_REQUEST,
                    ContentType.JSON,
                    "{ \"message\" : \"" + e.getMessage() + "\" }"
            );
        }
    }

    @Override
    public User getUser(String id) {
        long parsedId = Long.parseLong(id);
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
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"message\" : \"Internal Server Error\" }"
            );
        } catch (DataAccessException e) {
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
                    HttpStatus.OK,
                    ContentType.JSON,
                    AuthenticationService.getInstance().generateToken(username)
            );
        } catch (JsonProcessingException e) {
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"message\" : \"Internal Server Error\" }"
            );
        } catch (DataAccessException e) {
            return new Response(
                    HttpStatus.BAD_REQUEST,
                    ContentType.JSON,
                    "{ \"message\" : \"" + e.getMessage() + "\" }"
            );
        }
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

            long parsedId = Long.parseLong(id);
            userRepository.update(parsedId, updatedUser);

            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    "{ \"message\" : \"User with id " + id + " has been updated.\" }"
            );
        } catch (JsonProcessingException e) {
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"message\" : \"Internal Server Error\" }"
            );
        } catch (DataAccessException e) {
            return new Response(
                    HttpStatus.BAD_REQUEST,
                    ContentType.JSON,
                    "{ \"message\" : \"" + e.getMessage() + "\" }"
            );
        }
    }

    // GET /users/leaderboard
    @Override
    public Response getLeaderboard() {
        try {
            Map<String, Double> avgRatingsByUsers = userRepository.getLeaderboard();

            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    getObjectMapper().writeValueAsString(getObjectMapper().writeValueAsString(avgRatingsByUsers))
            );
        } catch (JsonProcessingException e) {
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"message\" : \"Internal Server Error\" }"
            );
        } catch (DataAccessException e) {
            return new Response(
                    HttpStatus.BAD_REQUEST,
                    ContentType.JSON,
                    "{ \"message\" : \"" + e.getMessage() + "\" }"
            );
        }
    }
}
