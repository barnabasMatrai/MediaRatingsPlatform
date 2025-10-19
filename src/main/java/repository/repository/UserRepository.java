package repository.repository;

import model.User;

import java.util.ArrayList;
import java.util.List;

public class UserRepository implements IUserRepository {
    private List<User> userData;

    public UserRepository()
    {
        this.userData = new ArrayList<>();
    }

    @Override
    public User get(long id) {
        User user = userData.stream()
                .filter(u -> u.getId() == id)
                .findFirst()
                .orElse(null);

        return user;
    }

    @Override
    public User get(String username) {

        return userData.stream()
                .filter(u -> u.getUserProfile().getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void add(User user) {
        userData.add(user);
    }
}
