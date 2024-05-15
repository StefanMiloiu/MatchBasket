package ro.mpp2024;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
public class MatchDBRepository implements Repository<Long, Match>{
    private String url;
    private String user;
    private String password;
    private static final Logger LOGGER = LogManager.getLogger();
    //            logger.error(ex);

    public MatchDBRepository(String url, String user, String password) {
        LOGGER.info("Intializing MatchDBRepository with url {}", url);
        this.url = url;
        this.user = user;
        this.password = password;

        try {
            // Load the JDBC driver class
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public Optional<Match> save(Match match) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            LOGGER.traceEntry("saving match {} ",match);
            // Create a connection to the database
            connection = DriverManager.getConnection(url, user, password);

            // Create the SQL query to insert a new match into the database
            String sql = "INSERT INTO Matches (team_a, team_b, match_type) VALUES (?, ?, ?)";

            // Create a prepared statement with the SQL query
            preparedStatement = connection.prepareStatement(sql);

            // Set the parameters for the prepared statement
            preparedStatement.setString(1, match.getTeamA());
            preparedStatement.setString(2, match.getTeamB());
            preparedStatement.setString(3, match.getMatchType());

            // Execute the prepared statement to insert the match into the database
            preparedStatement.executeUpdate();
            LOGGER.trace( "Saved () instances {}", match);
            LOGGER.traceExit();
            return Optional.of(match);
        } finally {
            // Close the prepared statement and the connection
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    @Override
    public Optional<Match> findOne(Long id) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            LOGGER.traceEntry("Searching mask with id {} ",id);

            connection = DriverManager.getConnection(url, user, password);

            // Modify the SQL query to also select the id field
            String sql = "SELECT id, team_a, team_b, match_type FROM Matches WHERE id = ?";

            // Create a prepared statement with the SQL query
            preparedStatement = connection.prepareStatement(sql);

            // Set the parameters for the prepared statement
            preparedStatement.setLong(1, id);

            // Execute the prepared statement to insert the match into the database
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                // Retrieve the id from the result set and set it in the Match object
                Long matchId = resultSet.getLong("id");
                Match match = new Match(matchId, resultSet.getString("team_a"), resultSet.getString("team_b"), resultSet.getString("match_type"));
                LOGGER.trace( "Found match {}", match);
                LOGGER.traceExit();
                return Optional.of(match);
            } else {
                return Optional.empty();
            }

        } finally {
            // Close the prepared statement and the connection
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    public Optional<Match> findOneByName(String teamA, String teamB) throws SQLException{
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            LOGGER.traceEntry("Searching match with teamA {} and teamB {} ",teamA, teamB);
            connection = DriverManager.getConnection(url, user, password);

            // Create the SQL query to insert a new match into the database
            String sql = "SELECT id, match_type FROM Matches WHERE team_a = ? AND team_b = ?";

            // Create a prepared statement with the SQL query
            preparedStatement = connection.prepareStatement(sql);

            // Set the parameters for the prepared statement
            preparedStatement.setString(1, teamA);
            preparedStatement.setString(2, teamB);

            // Execute the prepared statement to insert the match into the database
            preparedStatement.executeQuery();
            Match match = new Match(preparedStatement.getResultSet().getLong("id"),teamA, teamB, preparedStatement.getResultSet().getString("match_type"));
            LOGGER.trace( "Found match {}", match);
            LOGGER.traceExit();
            return Optional.of(match);
        } finally {
            // Close the prepared statement and the connection
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    public Iterable<Match> findAll() throws SQLException{
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        List<Match> matches= new ArrayList<Match>();
        try {
            LOGGER.traceEntry("Finding all matches");
            connection = DriverManager.getConnection(url, user, password);

            // Create the SQL query to insert a new match into the database
            String sql = "SELECT id, team_a, team_b, match_type FROM Matches";

            // Create a prepared statement with the SQL query
            preparedStatement = connection.prepareStatement(sql);

            // Execute the prepared statement to insert the match into the database
            preparedStatement.executeQuery();
            while(preparedStatement.getResultSet().next()) {
                Match match = new Match(preparedStatement.getResultSet().getLong("id"),preparedStatement.getResultSet().getString("team_a"), preparedStatement.getResultSet().getString("team_b"), preparedStatement.getResultSet().getString("match_type"));
                LOGGER.trace( "Found match {}", match);
                LOGGER.traceExit();
                matches.add(match);
            }
        } finally {
            // Close the prepared statement and the connection
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
        return matches;
    }
    //find all the matches that still have available seats
    public Iterable<Match> findAllWithAvailableSeats() throws SQLException{
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        List<Match> matches= new ArrayList<Match>();
        try {
            LOGGER.traceEntry("Finding all matches with available seats");
            connection = DriverManager.getConnection(url, user, password);

            // Create the SQL query to insert a new match into the database
            String sql = "SELECT m.id, m.team_a, m.team_b, m.match_type FROM Matches m JOIN Tickets t ON m.id = t.match_id WHERE t.available_seats > 0 order by t.available_seats desc";

            // Create a prepared statement with the SQL query
            preparedStatement = connection.prepareStatement(sql);

            // Execute the prepared statement to insert the match into the database
            preparedStatement.executeQuery();
            while(preparedStatement.getResultSet().next()) {
                Match match = new Match(preparedStatement.getResultSet().getLong("id"),preparedStatement.getResultSet().getString("team_a"), preparedStatement.getResultSet().getString("team_b"), preparedStatement.getResultSet().getString("match_type"));
                LOGGER.trace( "Found match {}", match);
                LOGGER.traceExit();
                matches.add(match);
            }
        } finally {
            // Close the prepared statement and the connection
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
        return matches;
    }

}
