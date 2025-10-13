package services;

import model.MediaEntry;
import repositories.IMediaEntryRepository;

import java.util.Set;

public class MediaEntryService implements IMediaEntryService {
    private static MediaEntryService instance = null;

    private IMediaEntryRepository mediaEntryRepository;

    private MediaEntryService(IMediaEntryRepository mediaEntryRepository) {
        this.mediaEntryRepository = mediaEntryRepository;
    }

    public static MediaEntryService getInstance(IMediaEntryRepository mediaEntryRepository) {
        if (instance == null) {
            instance = new MediaEntryService(mediaEntryRepository);
        }
        return instance;
    }

    @Override
    public void add(MediaEntry mediaEntry) {

    }

    @Override
    public Set<MediaEntry> getAll() {
        return Set.of();
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
