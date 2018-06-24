package ucab.ingsw.socialnetworkproject.service.strategy;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import ucab.ingsw.socialnetworkproject.response.MessageConstants;
import ucab.ingsw.socialnetworkproject.response.searchResponse.InstaResponse;
import ucab.ingsw.socialnetworkproject.response.searchResponse.SpotifyResponse;
import ucab.ingsw.socialnetworkproject.response.searchResponse.SpotifyTrackResponse;
import ucab.ingsw.socialnetworkproject.service.Builder;
import ucab.ingsw.socialnetworkproject.service.dataContainer.spotify.SpotifyContainer;
import ucab.ingsw.socialnetworkproject.service.dataContainer.spotify.SpotifyToken;
import ucab.ingsw.socialnetworkproject.service.dataContainer.spotify.SpotifyTracks;

import java.util.ArrayList;
import java.util.List;


@Slf4j
public class SpotifySearchStrategy implements SearchStrategy {

    private static final String CREDENTIALS="ODg0M2Y5ZGIwMmE1NDM3OGI1ZGRiMmUwYzRlMmY1YmI6ODYxY2IzMGQwNWZjNGQ2MWE1YzE1OWE4YzY3YTM5OWQ=";
    private static String ACCESS_TOKEN="BQB_cBf7ybEJYkSXAmub8Z8yICIcYvKiYMepsX4e77cVa186k2Xh5R6gzmWkSgLu9PM6U6CdKrNDYcxEwC0" ;
    private static final String PAGE_SIZE="10";
    private String offset;

    private SpotifyResponse buildResponse(SpotifyTracks spotifyTracks){
        SpotifyResponse spotifyResponse=new SpotifyResponse();

        if (spotifyTracks.getNext()!=null){
            String[] offsetA=spotifyTracks.getNext().split("&");
            String[] offsetB=offsetA[2].split("=");
            String offsetC=offsetB[1];
            spotifyResponse.setNextPageOffset(offsetC);
        }

        if (spotifyTracks.getPrevious()!=null){
            String[] offsetA=spotifyTracks.getPrevious().split("&");
            String[] offsetB=offsetA[2].split("=");
            String offsetC=offsetB[1];
            spotifyResponse.setPrevPageOffset(offsetC);
        }

        List<SpotifyTrackResponse> trackResponses = new ArrayList<>();
        spotifyTracks.getItems().forEach( i-> {
            SpotifyTrackResponse spotifyTrackResponse=new SpotifyTrackResponse();
            spotifyTrackResponse.setName(i.getName());
            spotifyTrackResponse.setAlbum(i.getAlbum().getName());
            spotifyTrackResponse.setAlbumImageUrl(i.getAlbum().getImages().get(1).getUrl());
            List<String> artists = new ArrayList<>();
            i.getArtists().forEach( j->{
                artists.add(j.getName());
                    }

            );
            spotifyTrackResponse.setArtists(artists);
            spotifyTrackResponse.setUrl(i.getExternal_urls().getSpotify());
            trackResponses.add(spotifyTrackResponse);
                }
        );
        spotifyResponse.setTracks(trackResponses);

        return spotifyResponse;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }

    public ResponseEntity<Object> seeker(String searchTerm) {
        String searchUrl="https://api.spotify.com/v1/search?type=track&q="+searchTerm+"&limit="+PAGE_SIZE;
        if ((offset!=null)&&(Integer.parseInt(offset)>0)) searchUrl=searchUrl+"&offset="+offset;
        Builder builder = new Builder();
        boolean done=true;
        while (done){
            try {
                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers= new HttpHeaders();
                headers.add("Authorization","Bearer "+ACCESS_TOKEN);
                HttpEntity<String> request = new HttpEntity<String>(headers);
                ResponseEntity<SpotifyContainer> response=restTemplate.exchange(searchUrl,HttpMethod.GET,request,SpotifyContainer.class);
                SpotifyContainer spotifyContainer=response.getBody();
                done=false;
                if (spotifyContainer.getTracks().getItems().isEmpty()){
                    log.info("No result for search term ={}", searchTerm);
                    return ResponseEntity.badRequest().body(builder.buildAlertResponse(MessageConstants.NO_RESULT));
                }

                log.info("Returning results for search term ={}", searchTerm);
                return ResponseEntity.ok(buildResponse(spotifyContainer.getTracks()));
            }
            catch (HttpClientErrorException error){
                String tokenUrl="https://accounts.spotify.com/api/token";
                RestTemplate tokenTemplate =new RestTemplate();
                HttpHeaders tokenHeaders= new HttpHeaders();
                tokenHeaders.add("Content-Type","application/x-www-form-urlencoded");
                tokenHeaders.add("Authorization","Basic "+CREDENTIALS);

                MultiValueMap<String, String> params = new LinkedMultiValueMap();
                params.add("grant_type","client_credentials");
                HttpEntity<MultiValueMap<String, String>> tokenRequest = new HttpEntity(params,tokenHeaders);
                ResponseEntity<SpotifyToken> tokenResponse=tokenTemplate.exchange(tokenUrl,HttpMethod.POST,tokenRequest,SpotifyToken.class);
                SpotifyToken spotifyToken=tokenResponse.getBody();
                ACCESS_TOKEN=spotifyToken.getAccess_token();
            }
        }




        return null;
    }
}
