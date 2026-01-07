package repository.repository;

import model.MediaEntry;
import model.Rating;

import java.util.List;
import java.util.Map;

public interface IMediaRepository {
    long getUserIdByUsername(String username);
    int getMediaTypeId(String mediaType);
    int getOrCreateGenreId(String genreName);
    MediaEntry get(long id);
    List<MediaEntry> get(Map<String, String> filters);
    void add(MediaEntry mediaEntry, String username);
    void delete(long mediaEntryId, long userId);
    void update(MediaEntry updatedMediaEntry, long mediaEntryId, long userId);
    void rate(Rating rating, long mediaEntryId, long userId);
    void favorite(long mediaEntryId, long userId);
    void unfavorite(long mediaEntryId, long userId);
}
