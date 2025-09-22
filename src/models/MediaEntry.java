package models;

import java.util.Set;

public class MediaEntry {
    private enum MediaType {
        MOVIE,
        SERIES,
        GAME
    }

    private enum Genre {
        ACTION,
        DRAMA,
        HORROR,
        COMEDY
    }

    private String createdBy;
    private String title;
    private String description;
    private MediaType mediaType;
    private int releaseYear;
    private Genre[] genres;
    private int ageRestriction;
    private Set<Rating> ratings;
}
