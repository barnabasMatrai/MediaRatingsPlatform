package repositories;

import models.User;

import java.util.Set;

public interface IUserRepository {
    boolean login(User user);
    Set<User> getUsers();
}
