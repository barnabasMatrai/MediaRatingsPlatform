package repositories;

import models.User;
import models.UserProfile;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public final class UserRepository extends Repository implements IUserRepository {
    private static UserRepository instance = new UserRepository();
    private UserRepository() {}

    public static UserRepository getInstance() {
        return instance;
    }

    private Set<User> users;

    public boolean login(User user) {
        User found = this.users.stream().filter(u -> u.equals(user)).findFirst().orElse(null);
        return found != null;
    }

    public Set<User> getUsers() {
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT username, password, email, firstname, lastname FROM users");
            statement.execute();
            ResultSet resultSet = statement.getResultSet();
            Set<User> users = new HashSet<>();
            while (resultSet.next()) {
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                String email = resultSet.getString("email");
                String firstName = resultSet.getString("firstname");
                String lastName = resultSet.getString("lastname");
                users.add(new User(new UserProfile(username, password, email, firstName, lastName)));
            }
            return users;
        } catch (SQLException e) {
            System.err.println("Unable to establish connection!");
        }

        return null;
    }
}
