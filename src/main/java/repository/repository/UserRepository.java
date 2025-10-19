package repository.repository;

import model.User;
import model.UserProfile;
import repository.DataAccessException;
import repository.UnitOfWork;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class UserRepository {
    private List<User> userData;
//    private UnitOfWork unitOfWork;

    public UserRepository()
    {
        this.userData = new ArrayList<>();
    }

    public User get(long id) {
        User user = userData.stream()
                .filter(u -> u.getId() == id)
                .findFirst()
                .orElse(null);

        return user;
    }

    public User get(String username) {

        return userData.stream()
                .filter(u -> u.getUserProfile().getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    public void add(User user) {
        userData.add(user);
    }
}
