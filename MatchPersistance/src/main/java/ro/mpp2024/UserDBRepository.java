package ro.mpp2024;

import ro.mpp2024.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDBRepository{
    private String url;
    private String user;
    private String password;

    public UserDBRepository(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public Optional<User> save(String username, String password) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = DriverManager.getConnection(url, user, password);
            String sql = "INSERT INTO Users (username, password) VALUES (?, ?)";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            preparedStatement.executeUpdate();
            User user = new User(username, password);
            return Optional.of(user);
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    public Optional<User> findOne(String username) throws SQLException{
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = DriverManager.getConnection(url, user, password);
            String sql = "SELECT id, password FROM Users WHERE username = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                User user = new User(resultSet.getLong("id"), username, resultSet.getString("password"));
                return Optional.of(user);
            } else {
                return Optional.empty();
            }
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    public Iterable<User> findAll() throws SQLException{
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        List<User> users = new ArrayList<User>();
        try {
            connection = DriverManager.getConnection(url, user, password);
            String sql = "SELECT id, username, password FROM Users";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.executeQuery();
            while(preparedStatement.getResultSet().next()) {
                User user = new User(preparedStatement.getResultSet().getLong("id"), preparedStatement.getResultSet().getString("username"), preparedStatement.getResultSet().getString("password"));
                users.add(user);
            }
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
        return users;
    }
}