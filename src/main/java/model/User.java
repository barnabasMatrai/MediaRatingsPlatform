package model;

import java.util.Set;

public class User implements IModel {
    private long id;
    private UserProfile userProfile;

    private Set<MediaEntry> mediaEntries;
    private Set<Rating> ratings;
    private Set<MediaEntry> favorites;
    private Set<Rating> likedRatings;

    public User(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    public long getId() {
        return id;
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public Set<Rating> getRatings() {
        return ratings;
    }
}
