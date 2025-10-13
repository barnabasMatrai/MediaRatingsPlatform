package services;

import model.User;

public interface IUserService extends IService<User> {
    void login(User user);
}
