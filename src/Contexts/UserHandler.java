package Contexts;

import Models.User;
import Models.UserProfile;
import com.sun.net.httpserver.HttpExchange;

import java.net.URI;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class UserHandler extends Handler {
    @Override
    public void handle(HttpExchange exchange) {
        String method = exchange.getRequestMethod().toUpperCase();
        URI requestURI = exchange.getRequestURI();
        Map<String, String> params = queryToMap(requestURI.getQuery());

        try {
            Connection connection = getConnection();

            switch (method) {
                case "GET":
                    if (params.containsKey("username")) {
                        // Get user data
                    } else {
                        // Get users
                        getUsers(connection);
                    }
                    break;
                case "POST":
                    if (params.containsKey("username") && params.containsKey("password")) {
                        // Register/login user
                    }
                    break;
                case "PUT":
                    if (params.containsKey("username") && params.containsKey("password") &&
                        params.containsKey("email") && params.containsKey("firstname") &&
                        params.containsKey("lastname")) {
                        // Update user profile
                    }
                    break;
                case "DELETE":
                    if (params.containsKey("username") && params.containsKey("password")) {
                        // Delete user
                    }
                    break;
            }

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Set<User> getUsers(Connection connection) {
        try {
            Statement statement = connection.createStatement();
            statement.execute("SELECT username, password, email, firstname, lastname FROM Users");
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
            e.printStackTrace();
        }

        return null;
    }
}
