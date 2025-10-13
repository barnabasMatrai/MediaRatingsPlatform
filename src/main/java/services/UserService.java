package services;

import model.User;
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

    @Override
    public void login(User user) {

    }

    @Override
    public void add(User user) {

    }

    @Override
    public Set<User> getAll() {
        return userRepository.getAll();
    }

    @Override
    public User get() {
        return null;
    }

    @Override
    public void update(User user) {

    }

    @Override
    public void remove(User user) {

    }
}
