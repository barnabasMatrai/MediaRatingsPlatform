package repository.repository;

import model.User;

public interface IUserRepository {
    User get(long id);
    User get(String username);
    void add(User user);
}
