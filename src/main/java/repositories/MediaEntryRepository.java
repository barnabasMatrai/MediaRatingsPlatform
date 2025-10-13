package repositories;

import model.MediaEntry;

import java.util.Set;

public class MediaEntryRepository extends Repository implements IRepository<MediaEntry> {
    private static MediaEntryRepository instance = new MediaEntryRepository();
    private MediaEntryRepository() {}

    public static MediaEntryRepository getInstance() {
        return instance;
    }

    private Set<MediaEntry> mediaEntries;

    @Override
    public void add(MediaEntry mediaEntry) {
        mediaEntries.add(mediaEntry);
    }

    @Override
    public Set<MediaEntry> getAll() {
        return mediaEntries;
    }

    @Override
    public MediaEntry get() {
        return null;
    }

    @Override
    public void update(MediaEntry mediaEntry) {

    }

    @Override
    public void remove(MediaEntry mediaEntry) {

    }
}
