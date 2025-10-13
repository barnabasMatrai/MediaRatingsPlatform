package model;

import java.util.Set;

public class MediaEntry implements IModel {
    private int id;
    private enum MediaType {
        MOVIE,
        SERIES,
        GAME
    }

    private String createdBy;
    private String title;
    private String description;
    private MediaType mediaType;
    private int releaseYear;
    private String[] genres;
    private int ageRestriction;
    private Set<Rating> ratings;

    public MediaEntry(String createdBy, String title, String description, MediaType mediaType, int releaseYear, String[] genres, int ageRestriction) {
        this.createdBy = createdBy;
        this.title = title;
        this.description = description;
        this.mediaType = mediaType;
        this.releaseYear = releaseYear;
        this.genres = genres;
        this.ageRestriction = ageRestriction;
    }
}
