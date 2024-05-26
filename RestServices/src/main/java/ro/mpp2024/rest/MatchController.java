package ro.mpp2024.rest;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.mpp2024.Match;
import ro.mpp2024.MatchDBInterface;
import ro.mpp2024.MatchDBRepository;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:8081", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE}, allowedHeaders = {"Content-Type", "Accept"})
@RestController
@RequestMapping("/matches")
public class MatchController {

    @Autowired
    private MatchDBInterface matchDBRepository; // =  new MatchDBRepository("jdbc:sqlserver://localhost;database=BasketballMatch", "sa", "333333SASEsm");

    public MatchController() throws SQLException {
    }

    @RequestMapping(value="/helloworld")
    public String greeting(@RequestParam(value = "name", defaultValue = "world") String name)
    {
        System.out.println("request:Hello World");
        return "Hello " + name;
    }
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> getChallenges() throws SQLException {
        List<Match> challenges = (List<Match>) matchDBRepository.findAll();
        if(challenges == null)
        {
            return new ResponseEntity<>("List is empty",HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(challenges,HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> save(@RequestBody Match match) throws SQLException {
        Optional<Match> challengeAdded = matchDBRepository.save(match);
        return new ResponseEntity<>(challengeAdded, HttpStatus.OK);

    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getById(@PathVariable Long id)
    {
        try {
            Optional<Match> match = matchDBRepository.findOne(id);
            return new ResponseEntity<>(match, HttpStatus.OK);
        }
        catch (Exception e)
        {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Match match) throws SQLException {
        matchDBRepository.updateMatch(id, match);
        match.setId(id);
        return new ResponseEntity<>(match, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> delete(@PathVariable Long id) throws SQLException {
        Match removedMatch = matchDBRepository.delete(id);
        return new ResponseEntity<>(removedMatch, HttpStatus.OK);
    }

}
