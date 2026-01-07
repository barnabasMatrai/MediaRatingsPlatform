package service;

import restserver.server.Response;

import java.util.Map;

public interface IMediaService {
    Response getMediaEntry(String id);
    Response getMediaEntries(Map<String, String> filters);
    Response createMedia(String requestBody, String username);
    Response deleteMedia(long mediaEntryId, String username);
    Response updateMedia(String requestBody, long mediaEntryId, String username);
    Response rateMedia(String requestBody, long mediaEntryId, String username);
    Response markAsFavorite(long mediaEntryId, String username);
    Response unmarkAsFavorite(long mediaEntryId, String username);
}
