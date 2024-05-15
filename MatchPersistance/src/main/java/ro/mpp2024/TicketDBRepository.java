package ro.mpp2024;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TicketDBRepository implements Repository<Long, Ticket>{
    private String url;
    private String user;
    private String password;

    public TicketDBRepository(String url, String user, String password) {
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

    @Override
    public Optional<Ticket> save(Ticket ticket) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = DriverManager.getConnection(url, user, password);
            String sql = "INSERT INTO Tickets (price, available_seats, match_id) VALUES (?, ?, ?)";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setFloat(1, ticket.getPrice());
            preparedStatement.setInt(2, ticket.getAvailableSeats());
            preparedStatement.setLong(3, ticket.getMatchId());
            preparedStatement.executeUpdate();
            return Optional.of(ticket);
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    @Override
    public Optional<Ticket> findOne(Long id) throws SQLException{
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = DriverManager.getConnection(url, user, password);
            String sql = "SELECT price, available_seats, match_id FROM Tickets WHERE id = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Ticket ticket = new Ticket(id, resultSet.getFloat("price"), resultSet.getInt("available_seats"), resultSet.getLong("match_id"));
                return Optional.of(ticket);
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

    @Override
    public Iterable<Ticket> findAll() throws SQLException{
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        List<Ticket> tickets = new ArrayList<>();
        try {
            connection = DriverManager.getConnection(url, user, password);
            String sql = "SELECT id, price, available_seats, match_id FROM Tickets";
            preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                Ticket ticket = new Ticket(resultSet.getLong("id"), resultSet.getFloat("price"), resultSet.getInt("available_seats"), resultSet.getLong("match_id"));
                tickets.add(ticket);
            }
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
        return tickets;
    }

    //find ticker by match id
    public Optional<Ticket> find(Long matchId) throws SQLException{
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = DriverManager.getConnection(url, user, password);
            String sql = "SELECT id, price, available_seats, match_id FROM Tickets WHERE match_id = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setLong(1, matchId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Ticket ticket = new Ticket(resultSet.getLong("id"), resultSet.getFloat("price"), resultSet.getInt("available_seats"), resultSet.getLong("match_id"));
                return Optional.of(ticket);
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

    public void update(Ticket ticket) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            System.out.println("Updating ticket: " + ticket);
            connection = DriverManager.getConnection(url, user, password);
            String sql = "UPDATE Tickets SET price = ?, available_seats = ?, match_id = ? WHERE id = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setFloat(1, ticket.getPrice());
            preparedStatement.setInt(2, ticket.getAvailableSeats());
            preparedStatement.setLong(3, ticket.getMatchId());
            preparedStatement.setLong(4, ticket.getId());
            preparedStatement.executeUpdate();
            System.out.println("Ticket updated");
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }
}