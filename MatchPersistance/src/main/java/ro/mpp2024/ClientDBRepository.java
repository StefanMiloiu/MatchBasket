package ro.mpp2024;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class ClientDBRepository implements Repository<Long, Client> {
    private String url;
    private String user;
    private String password;

    private static final Logger LOGGER = LogManager.getLogger();
    public ClientDBRepository(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;

        try {
            LOGGER.info("Intializing ClientDBRepository with url {}", url);
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public Optional<Client> save(Client client) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            LOGGER.traceEntry("Saving client {} ",client);
            connection = DriverManager.getConnection(url, user, password);
            String sql = "INSERT INTO Clients (name, nr_of_tickets) VALUES (?, ?)";

            // Use the RETURN_GENERATED_KEYS flag to get the generated id
            preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, client.getName());
            preparedStatement.setInt(2, client.getTicketsBought());
            preparedStatement.executeUpdate();

            // Retrieve the generated id
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                Long id = generatedKeys.getLong(1);
                Client newClient = new Client(id, client.getName(), client.getTicketsBought());
                client.setId(id);  // Set the id in the Client object
                return Optional.of(newClient);  // Retrieve the client from the database and return it
            }
            LOGGER.trace( "Saved ()");
            LOGGER.traceExit();
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Client> findOne(Long id) throws SQLException{
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            System.out.println("Finding client with id " + id);
            LOGGER.traceEntry("Finding client with id {} ",id);
            connection = DriverManager.getConnection(url, user, password);
            String sql = "SELECT name, nr_of_tickets FROM Clients WHERE id = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Client client = new Client(id, resultSet.getString("name"), resultSet.getInt("nr_of_tickets"));
                LOGGER.trace( "Found {}", client);
                System.out.println("Found " + client);
                LOGGER.traceExit();
                return Optional.of(client);
            } else {
                System.out.println("No client found with id " + id);
                LOGGER.traceExit();
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


    public Optional<Client> findOneName(String name) throws SQLException{
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            LOGGER.traceEntry("Finding client with name {} ",name);
            connection = DriverManager.getConnection(url, user, password);
            String sql = "SELECT id, nr_of_tickets FROM Clients WHERE name = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, name);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Client client = new Client(resultSet.getLong("id"),name, preparedStatement.getResultSet().getInt("nr_of_tickets"));
                client.setId(preparedStatement.getResultSet().getLong("id"));
                System.out.println(preparedStatement.getResultSet().getLong("id"));
                LOGGER.trace( "Found {}", client);
                LOGGER.traceExit();
                return Optional.of(client);
            }
            return Optional.empty();
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
    public Iterable<Client> findAll() throws SQLException{
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        List<Client> clients = new ArrayList<>();
        try {
            LOGGER.traceEntry("Finding all clients");
            connection = DriverManager.getConnection(url, user, password);
            String sql = "SELECT id, name, nr_of_tickets FROM Clients";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.executeQuery();
            while(preparedStatement.getResultSet().next()) {
                Client client = new Client(preparedStatement.getResultSet().getLong("id"), preparedStatement.getResultSet().getString("name"), preparedStatement.getResultSet().getInt("nr_of_tickets"));
                clients.add(client);
                LOGGER.trace( "Found {}", client);
            }
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
        LOGGER.traceExit();
        return clients;
    }


}