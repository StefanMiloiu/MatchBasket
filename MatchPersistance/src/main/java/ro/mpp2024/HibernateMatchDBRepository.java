package ro.mpp2024;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import ro.mpp2024.HibernateMatch;
import ro.mpp2024.Match;
import ro.mpp2024.hibernateUtils.HibernateUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;


public class HibernateMatchDBRepository {
    private static final Logger LOGGER = LogManager.getLogger();
    private SessionFactory sessionFactory = HibernateUtils.getSessionFactory();

    public HibernateMatchDBRepository() {}

    public Optional<HibernateMatch> save(HibernateMatch match) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            System.out.println(session);
            transaction = session.beginTransaction();
            session.save(match);
            transaction.commit();
            LOGGER.info("Match saved: {}", match);
            return Optional.of(match);
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            LOGGER.error("Failed to save match", e);
        }
        return Optional.empty();
    }

    public Optional<HibernateMatch> findOne(Long id) {
        try (Session session = sessionFactory.openSession()) {
            HibernateMatch match = session.get(HibernateMatch.class, id);
            return Optional.ofNullable(match);
        } catch (Exception e) {
            LOGGER.error("Failed to find match", e);
        }
        return Optional.empty();
    }

    public Iterable<HibernateMatch> findAll() {
        System.out.println("Finding all matches");
        try (Session session = sessionFactory.openSession()) {
            System.out.println("1");
            List<HibernateMatch> matches = session.createQuery("FROM HibernateMatch", HibernateMatch.class).list();
            System.out.println("Found matches: " + matches.size());
            return matches;
        } catch (Exception e) {
            LOGGER.error("Failed to find all matches", e);
        }
        return List.of();
    }

    // Example of a custom query using Hibernate
    public Iterable<HibernateMatch> findAllWithAvailableSeats() throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        List<HibernateMatch> matches = new ArrayList<>();
        try {
            LOGGER.traceEntry("Finding all matches with available seats");
            connection = DriverManager.getConnection("jdbc:sqlserver://localhost;database=BasketballMatch;", "sa", "333333SASEsm");
            String sql = "SELECT m.id, m.team_a, m.team_b, m.match_type FROM Matches m JOIN Tickets t ON m.id = t.match_id WHERE t.available_seats > 0 order by t.available_seats desc";
            preparedStatement = connection.prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                HibernateMatch match = new HibernateMatch(rs.getLong("id"), rs.getString("team_a"), rs.getString("team_b"), rs.getString("match_type"));
                matches.add(match);
            }
        } finally {
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