package repositories;

import model.User;

public interface IUserRepository extends IRepository<User> {
    boolean login(User user);
}
