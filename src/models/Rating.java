package models;

import java.sql.Timestamp;

public class Rating {
    private enum StarValue {
        WORST,
        BAD,
        AVERAGE,
        GOOD,
        GREAT
    }

    private User createdBy;
    private boolean isConfirmed;
    private String comment;
    private Timestamp timestamp;
    private StarValue starValue;

    public Rating(User createdBy, StarValue starValue) {
        this.createdBy = createdBy;
        this.isConfirmed = false;
        this.timestamp = new Timestamp(System.currentTimeMillis());
        this.starValue = starValue;
    }

    public Rating(User createdBy, String comment, StarValue starValue) {
        this(createdBy, starValue);
        this.comment = comment;
    }
}
