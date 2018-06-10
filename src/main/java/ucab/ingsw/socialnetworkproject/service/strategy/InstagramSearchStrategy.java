package ucab.ingsw.socialnetworkproject.service.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ucab.ingsw.socialnetworkproject.service.Builder;
import ucab.ingsw.socialnetworkproject.service.dataContainer.instagram.InstaData;
import ucab.ingsw.socialnetworkproject.service.dataContainer.instagram.InstagramUrl;
import ucab.ingsw.socialnetworkproject.response.AlertResponse;
import ucab.ingsw.socialnetworkproject.response.SearchResponse;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j

public class InstagramSearchStrategy implements SearchStrategy {

    public static final String AUTH_TOKEN = "705751369.9ec8a89.ee214d6054f9473bb022043178680802";

    private AlertResponse buildAlertResponse(String message){
        AlertResponse response = new AlertResponse();
        response.setMessage(message);
        response.setTimestamp(LocalDateTime.now());
        return response;
    }

    public ResponseEntity<Object> seeker(String searchTerm){
        String searchUrl = "https://api.instagram.com/v1/tags/"+searchTerm+"/media/recent?access_token="+AUTH_TOKEN;
        List<String> urlList = new ArrayList<>();
        List<InstaData> instaData;
        RestTemplate restTemplate = new RestTemplate();
        InstagramUrl instagramUrl = restTemplate.getForObject(searchUrl, InstagramUrl.class);
        instaData = instagramUrl.getData();
        if(instaData.isEmpty()){
            log.info("No result for search term ={}", searchTerm);

            return ResponseEntity.badRequest().body(buildAlertResponse("no_result."));
        }
        else {
            log.info("Returning results for search term ={}", searchTerm);

            SearchResponse searchResponse = new SearchResponse();
            instaData.forEach(i -> {
                urlList.add(i.getImages().getStandard_resolution().getUrl());
            });
            searchResponse.setUrls(urlList);
            return ResponseEntity.ok(searchResponse);
        }
    }

}
