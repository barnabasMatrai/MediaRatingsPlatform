package services;

import models.User;
import repositories.IUserRepository;

import java.util.Set;

public class UserService implements IUserService{
    private static UserService instance = null;

    private IUserRepository userRepository;

    private UserService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public static UserService getInstance(IUserRepository userRepository) {
        if (instance == null) {
            instance = new UserService(userRepository);
        }
        return instance;
    }

    public Set<User> getUsers() {
        return  userRepository.getUsers();
    }
}
