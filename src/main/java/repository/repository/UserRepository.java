package repository.repository;

import model.User;
import repository.DataAccessException;
import repository.UnitOfWork;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRepository implements IUserRepository {
    private static UserRepository instance = null;

    private UserRepository() {}

    public static UserRepository getInstance() {
        if (instance == null) {
            instance = new UserRepository();
        }

        return instance;
    }

    @Override
    public User get(long id) {
        UnitOfWork unitOfWork = new UnitOfWork();
        try (PreparedStatement preparedStatement = unitOfWork.prepareStatement(
                """
                SELECT username, password, email, firstname, lastname, favoritegenre FROM users
                WHERE id = ?;
                """)) {
            preparedStatement.setLong(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String username = resultSet.getString("username");
                    String password = resultSet.getString("password");
                    String email = resultSet.getString("email");
                    String firstname = resultSet.getString("firstname");
                    String lastname = resultSet.getString("lastname");
                    String favoriteGenre = resultSet.getString("favoritegenre");

                    return new User(username, password, email, firstname, lastname, favoriteGenre);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Problem accessing users.", e);
        }
        return null;
    }

    @Override
    public User get(String username) {
        UnitOfWork unitOfWork = new UnitOfWork();
        try (PreparedStatement preparedStatement = unitOfWork.prepareStatement(
                """
                SELECT password, email, firstname, lastname, favoritegenre FROM users
                WHERE username = ?;
                """)) {
            preparedStatement.setString(1, username);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String password = resultSet.getString("password");
                    String email = resultSet.getString("email");
                    String firstname = resultSet.getString("firstname");
                    String lastname = resultSet.getString("lastname");
                    String favoriteGenre = resultSet.getString("favoritegenre");

                    return new User(username, password, email, firstname, lastname, favoriteGenre);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Problem accessing users.", e);
        }
        return null;
    }

    @Override
    public void add(User user) {
        UnitOfWork unitOfWork = new UnitOfWork();
        try (PreparedStatement preparedStatement = unitOfWork.prepareStatement(
                """
                insert into users(username, password, email, firstname, lastname, favoritegenre)
                values (?, ?, ?, ?, ?, ?);
                """))
        {
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setString(3, user.getEmail());
            preparedStatement.setString(4, user.getFirstName());
            preparedStatement.setString(5, user.getLastName());
            preparedStatement.setString(6, user.getFavoriteGenre());
            preparedStatement.executeUpdate();

            unitOfWork.commitTransaction();
        } catch (SQLException e) {
            throw new DataAccessException("Insert unsuccessful", e);
        }
    }
}
