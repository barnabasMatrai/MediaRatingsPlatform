package service;

import com.fasterxml.jackson.core.JsonProcessingException;
import model.Rating;
import repository.DataAccessException;
import repository.ForbiddenException;
import repository.repository.IRatingRepository;
import restserver.http.ContentType;
import restserver.http.HttpStatus;
import restserver.server.Response;

public class RatingService extends ICanMapObjects implements IRatingService {
    private static IRatingService instance = null;
    private IRatingRepository ratingRepository;

    private RatingService(IRatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }

    public static IRatingService getInstance(IRatingRepository ratingRepository) {
        if (instance == null) {
            instance = new RatingService(ratingRepository);
        }
        return instance;
    }

    // POST /ratings/:id/like
    @Override
    public Response likeRating(String id, String username) {
        try {
            long ratingId = Long.parseLong(id);
            long userId = ratingRepository.getUserIdByUsername(username);

            ratingRepository.likeRating(ratingId, userId);

            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    "{ \"message\": \"Rating liked successfully\" }"
            );

        } catch (NumberFormatException e) {
            return new Response(
                    HttpStatus.BAD_REQUEST,
                    ContentType.JSON,
                    "{ \"message\": \"Invalid rating id\" }"
            );

        } catch (DataAccessException e) {
            return new Response(
                    HttpStatus.BAD_REQUEST,
                    ContentType.JSON,
                    "{ \"message\": \"" + e.getMessage() + "\" }"
            );

        } catch (Exception e) {
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"message\": \"Internal Server Error\" }"
            );
        }
    }

    // PUT /ratings/:id
    @Override
    public Response updateRating(String requestBody, String ratingId, String username) {
        try {
            long userId = ratingRepository.getUserIdByUsername(username);
            long ratingIdLong = Long.parseLong(ratingId);

            Rating updatedRating = this.getObjectMapper().readValue(requestBody, Rating.class);

            ratingRepository.update(updatedRating, ratingIdLong, userId);

            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    "{ message: \"Successfully updated rating with index " + ratingId + "\" }"
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
        } catch (ForbiddenException e) {
            return new Response(
                    HttpStatus.FORBIDDEN,
                    ContentType.JSON,
                    "{ \"message\" : \"" + e.getMessage() + "\" }"
            );
        }
    }

    // POST /ratings/:id/confirm
    @Override
    public Response confirmRating(String ratingId, String username) {
        try {
            long userId = ratingRepository.getUserIdByUsername(username);
            long ratingIdLong = Long.parseLong(ratingId);

            ratingRepository.confirm(ratingIdLong, userId);

            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    "{ message: \"Successfully confirmed rating with index " + ratingId + "\" }"
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
