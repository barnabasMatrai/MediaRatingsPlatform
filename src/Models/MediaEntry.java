package Models;

public class MediaEntry {
    enum MediaType {
        MOVIE,
        SERIES,
        GAME
    }

    private String title;
    private String description;
    private MediaType mediaType;
    private int releaseYear;
    private String[] genres;
    private int ageRestriction;
    private int[] ratings;
    private double averageScore;

    private String createdBy;
}
