package model;

import java.sql.Timestamp;

public class Rating implements IModel {
    private String createdBy;
    private Boolean isConfirmed;
    private String comment;
    private Timestamp timestamp;
    private Short stars;

    public Rating() {};
    public Rating(String createdBy, short stars) {
        this.createdBy = createdBy;
        this.isConfirmed = false;
        this.timestamp = new Timestamp(System.currentTimeMillis());
        this.stars = stars;
    }

    public Rating(String createdBy, String comment, Short stars) {
        this(createdBy, stars);
        this.comment = comment;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public Boolean getIsConfirmed() {
        return isConfirmed;
    }

    public String getComment() {
        return comment;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public Short getStars() {
        return stars;
    }
}
