package ro.mpp2024.start;

import ro.mpp2024.Match;
import ro.mpp2024.MatchesClient;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class TestRestServices {
    private final static MatchesClient matchesClient = new MatchesClient();
    public static void main(String[] args)
    {
        System.out.println("Started getting all matches");
        List<Match> challenges = matchesClient.getAll();
        System.out.println(challenges);
        assertFalse(challenges.isEmpty());

        System.out.println("Finding matches by id ");
        Match match = matchesClient.findMatchById(17L);
        System.out.println(match);
        assertEquals (17,match.getId());

        System.out.println("Adding a match");
        Match matchToBeAdded = new Match(100L,"Echipa 1 rest", "Echipa 2 rest", "Amical rest");
        matchToBeAdded =  matchesClient.addMatch(matchToBeAdded);
        assertNotNull(matchToBeAdded.getId());

        Match updatedMatch = new Match(16L,"Echipa 1 rest updated", "Echipa 2 rest updated", "Amical rest updated");
        matchesClient.updateMatch(match.getId() ,updatedMatch);
        updatedMatch = matchesClient.findMatchById(16L);
        assertEquals("Echipa 1 rest updated", updatedMatch.getTeamA());

        matchesClient.deleteMatch(13);


    }
}
