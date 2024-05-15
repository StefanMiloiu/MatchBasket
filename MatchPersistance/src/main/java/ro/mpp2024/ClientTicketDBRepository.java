package ro.mpp2024;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClientTicketDBRepository implements RepositoryMatchTicket<Long, ClientTickets> {
    private String url;
    private String user;
    private String password;

    public ClientTicketDBRepository(String url, String user, String password) {
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
    public Optional<ClientTickets> save(ClientTickets clientTickets) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = DriverManager.getConnection(url, user, password);
            String sql = "INSERT INTO ClientTickets (ticket_id, client_id) VALUES (?, ?)";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setLong(1, clientTickets.getTicketId());
            preparedStatement.setLong(2, clientTickets.getClientId());
            preparedStatement.executeUpdate();
            return Optional.of(clientTickets);
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
    public Optional<ClientTickets> findOne(Long id) throws SQLException{
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = DriverManager.getConnection(url, user, password);
            String sql = "SELECT ticket_id, client_id FROM ClientTickets WHERE id = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                ClientTickets clientTickets = new ClientTickets(id, resultSet.getLong("ticket_id"), resultSet.getLong("client_id"));
                return Optional.of(clientTickets);
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
    public Iterable<ClientTickets> findAll() throws SQLException{
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        List<ClientTickets> clientTicketsList = new ArrayList<>();
        try {
            connection = DriverManager.getConnection(url, user, password);
            String sql = "SELECT id, ticket_id, client_id FROM ClientTickets";
            preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                ClientTickets clientTickets = new ClientTickets(resultSet.getLong("id"), resultSet.getLong("ticket_id"), resultSet.getLong("client_id"));
                clientTicketsList.add(clientTickets);
            }
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
        return clientTicketsList;
    }
}