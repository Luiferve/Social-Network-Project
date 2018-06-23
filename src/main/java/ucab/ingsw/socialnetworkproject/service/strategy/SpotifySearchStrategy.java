package ucab.ingsw.socialnetworkproject.service.strategy;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import ucab.ingsw.socialnetworkproject.service.dataContainer.spotify.SpotifyContainer;


@Slf4j
public class SpotifySearchStrategy implements SearchStrategy {

    public static final String CREDENTIALS="ODg0M2Y5ZGIwMmE1NDM3OGI1ZGRiMmUwYzRlMmY1YmI6ODYxY2IzMGQwNWZjNGQ2MWE1YzE1OWE4YzY3YTM5OWQ=";
    public static String ACCESS_TOKEN="BQB_cBf7ybEJYkSXAmub8Z8yICIcYvKiYMepsX4e77cVa186k2Xh5R6gzmWkSgLu9PM6U6CdKrNDYcxEwC0" ;


    public ResponseEntity<Object> seeker(String searchTerm) {
        String searchUrl="https://api.spotify.com/v1/search?type=track&q="+searchTerm;
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers= new HttpHeaders();
        headers.add("Authorization","Bearer "+ACCESS_TOKEN);
        HttpEntity<String> request = new HttpEntity<String>(headers);
        ResponseEntity<SpotifyContainer> response=restTemplate.exchange(searchUrl,HttpMethod.GET,request,SpotifyContainer.class);
        SpotifyContainer spotifyContainer=response.getBody();
        return null;
    }
}
