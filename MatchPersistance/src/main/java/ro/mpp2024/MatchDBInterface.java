package ro.mpp2024;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.lang.annotation.Annotation;
import java.sql.SQLException;
import java.sql.SQLException;
import java.util.Optional;

public interface MatchDBInterface {
    Optional<Match> save(Match match) throws SQLException;
    Optional<Match> findOne(Long id) throws SQLException;
    Optional<Match> findOneByName(String teamA, String teamB) throws SQLException;
    Iterable<Match> findAll() throws SQLException;
    Iterable<Match> findAllWithAvailableSeats() throws SQLException;
    Match delete(Long id) throws SQLException;

    //Update
    void updateMatch(Long id, Match match) throws SQLException;
}