package model;

import java.util.Set;

public class User implements IModel {
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private String favoriteGenre;

    private Set<MediaEntry> mediaEntries;
    private Set<Rating> ratings;
    private Set<MediaEntry> favorites;
    private Set<Rating> likedRatings;

    public User() {};
    public User(String username, String password, String email, String firstName, String lastName) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public User(String username, String password, String email, String firstName, String lastName, String favoriteGenre) {
        this(username, password, email, firstName, lastName);
        this.favoriteGenre = favoriteGenre;
    }

    public String getUsername() { return username; }

    public String getPassword() { return password; }

    public String getEmail() { return email; }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() { return lastName; }

    public String getFavoriteGenre() {
        return favoriteGenre;
    }

    public Set<Rating> getRatings() {
        return ratings;
    }

    public Set<MediaEntry> getFavorites() {
        return favorites;
    }
}
