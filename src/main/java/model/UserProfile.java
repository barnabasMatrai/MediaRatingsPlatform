package model;

import java.io.Serializable;

public class UserProfile implements Serializable {
    private int id;
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;

    public UserProfile(String username, String password, String email, String firstName, String lastName) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
