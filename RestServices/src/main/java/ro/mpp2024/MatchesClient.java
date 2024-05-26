package ro.mpp2024;

import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import ro.mpp2024.rest.ServiceException;

import java.util.List;
import java.util.concurrent.Callable;

public class MatchesClient {
    private static String URL = "http://localhost:8080/matches";

    private RestTemplate restTemplate = new RestTemplate();
    private <T> T execute(Callable<T> callable) {
        try {
            return callable.call();
        } catch (ResourceAccessException | HttpClientErrorException e) { // server down, resource exception
            throw new ServiceException(e);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }
    public List<Match> getAll()
    {
        System.out.println("URL: "+URL);
        return execute(()-> restTemplate.getForObject(URL, List.class));
    }

    public Match findMatchById(Long id) {
        Match match =  execute(()->restTemplate.getForObject(URL+"/"+id, Match.class));
        match.setId(id);
        return match;
    }
    public Match addMatch(Match match)
    {
        return execute(()-> restTemplate.postForEntity(URL, match, Match.class).getBody());
    }

    public void updateMatch(Long id, Match updatedMatch) {
        execute(()-> {
            restTemplate.put(URL+"/"+id, updatedMatch);
            return null;
        });
    }

    public void deleteMatch(int id) {
        execute(()->{
            restTemplate.delete(URL+"/"+id);
            return null;
        });
    }
}
