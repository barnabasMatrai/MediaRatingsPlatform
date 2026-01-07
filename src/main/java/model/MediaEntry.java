package model;

import java.util.Set;

public class MediaEntry implements IModel {
    private String createdBy;
    private String title;
    private String description;
    private MediaType mediaType;
    private int releaseYear;
    private String[] genres;
    private int ageRestriction;
    private Set<Rating> ratings;

    public MediaEntry() {}
    public MediaEntry(String createdBy, String title, String description, MediaType mediaType, int releaseYear, String[] genres, int ageRestriction) {
        this.createdBy = createdBy;
        this.title = title;
        this.description = description;
        this.mediaType = mediaType;
        this.releaseYear = releaseYear;
        this.genres = genres;
        this.ageRestriction = ageRestriction;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public String getMediaTypeAsString() {
        return mediaType.toString();
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public String[] getGenres() {
        return genres;
    }

    public int getAgeRestriction() {
        return ageRestriction;
    }

    public static MediaType stringToMediaType(String type) {
        if (type == null) {
            throw new IllegalArgumentException("Type cannot be null");
        }
        try {
            return MediaType.valueOf(type);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid media type: " + type);
        }
    }
}
