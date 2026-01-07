package service;

import com.fasterxml.jackson.core.JsonProcessingException;
import model.MediaEntry;
import model.Rating;
import repository.DataAccessException;
import repository.ForbiddenException;
import repository.repository.IMediaRepository;
import restserver.http.ContentType;
import restserver.http.HttpStatus;
import restserver.server.Response;

import java.util.List;
import java.util.Map;

public class MediaService extends ICanMapObjects implements IMediaService {
    private static IMediaService instance = null;
    private IMediaRepository mediaRepository;

    private MediaService(IMediaRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
    }

    public static IMediaService getInstance(IMediaRepository mediaRepository) {
        if (instance == null) {
            instance = new MediaService(mediaRepository);
        }
        return instance;
    }

    // GET /media
    @Override
    public Response getMediaEntries(Map<String, String> filters) {
        try {
            List<MediaEntry> mediaEntries = mediaRepository.get(filters);

            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    this.getObjectMapper().writeValueAsString(mediaEntries)
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

    // GET /media/?title=?&genre=?&sortBy=?
    @Override
    public Response getMediaEntry(String id) {
        try {
            long parsedId = Long.parseLong(id);
            MediaEntry mediaEntry = mediaRepository.get(parsedId);

            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    this.getObjectMapper().writeValueAsString(mediaEntry)
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

    // POST /media
    @Override
    public Response createMedia(String requestBody, String username)
    {
        try {
            MediaEntry mediaEntry = this.getObjectMapper().readValue(requestBody, MediaEntry.class);

            mediaRepository.add(mediaEntry, username);

            return new Response(
                    HttpStatus.CREATED,
                    ContentType.JSON,
                    "{ message: \"Successfully added media " + mediaEntry.getTitle() + "\" }"
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

    // DELETE /media/id:
    @Override
    public Response deleteMedia(long mediaEntryId, String username)
    {
        try {
            Long userId = mediaRepository.getUserIdByUsername(username);

            mediaRepository.delete(mediaEntryId, userId);

            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    "{ message: \"Successfully deleted media with index " + mediaEntryId + "\" }"
            );
        } catch (ForbiddenException e) {
            return new Response(
                    HttpStatus.FORBIDDEN,
                    ContentType.JSON,
                    "{ \"message\" : \"" + e.getMessage() + "\" }"
            );
        }
    }

    // PUT /media/id:
    @Override
    public Response updateMedia(String requestBody, long mediaEntryId, String username) {
        try {
            long userId = mediaRepository.getUserIdByUsername(username);

            MediaEntry mediaEntry = this.getObjectMapper().readValue(requestBody, MediaEntry.class);

            mediaRepository.update(mediaEntry, mediaEntryId, userId);

            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    "{ message: \"Successfully update media with index " + mediaEntryId + "\" }"
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

    // POST /media/id:/rate
    @Override
    public Response rateMedia(String requestBody, long mediaEntryId, String username) {
        try {
            long userId = mediaRepository.getUserIdByUsername(username);

            Rating rating = this.getObjectMapper().readValue(requestBody, Rating.class);

            mediaRepository.rate(rating, mediaEntryId, userId);

            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    "{ message: \"Successfully rated media with index " + mediaEntryId + "\" }"
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

    // POST /media/id:/favorite
    @Override
    public Response markAsFavorite(long mediaEntryId, String username) {
        try {
            long userId = mediaRepository.getUserIdByUsername(username);

            mediaRepository.favorite(mediaEntryId, userId);

            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    "{ message: \"Successfully marked media with index " + mediaEntryId + " as favorite\" }"
            );
        } catch (DataAccessException e) {
            return new Response(
                    HttpStatus.BAD_REQUEST,
                    ContentType.JSON,
                    "{ \"message\" : \"" + e.getMessage() + "\" }"
            );
        }
    }

    // DELETE /media/id:/favorite
    @Override
    public Response unmarkAsFavorite(long mediaEntryId, String username) {
        try {
            long userId = mediaRepository.getUserIdByUsername(username);

            mediaRepository.unfavorite(mediaEntryId, userId);

            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    "{ message: \"Successfully unmarked media with index " + mediaEntryId + " as favorite\" }"
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
