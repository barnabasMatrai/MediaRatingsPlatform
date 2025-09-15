package Models;

import java.util.Set;

public class User {
    private UserProfile userProfile;

    private Set<MediaEntry> mediaEntries;
    private Set<Rating> ratings;
    private Set<MediaEntry> favorites;
    private Set<Rating> likedRatings;

    public User(UserProfile userProfile) {
        this.userProfile = userProfile;
    }
}
