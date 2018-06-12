package ucab.ingsw.socialnetworkproject.service.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ucab.ingsw.socialnetworkproject.response.searchResponse.InstaVideoResponse;
import ucab.ingsw.socialnetworkproject.service.Builder;
import ucab.ingsw.socialnetworkproject.service.dataContainer.instagram.InstaData;
import ucab.ingsw.socialnetworkproject.service.dataContainer.instagram.InstagramUrl;
import ucab.ingsw.socialnetworkproject.response.searchResponse.InstaResponse;
import java.util.ArrayList;
import java.util.List;

@Slf4j

public class InstagramSearchStrategy implements SearchStrategy {

    public static final String AUTH_TOKEN = "705751369.9ec8a89.ee214d6054f9473bb022043178680802";

    @Autowired
    private Builder builder;


    private List<InstaResponse> buildResponse(List<InstaData> instaData){
        List<InstaResponse> instaResponseList = new ArrayList<>();
        instaData.forEach(i -> {
            InstaResponse instaResponse;
            if(i.getType().equals("image"))
                instaResponse = new InstaResponse();
            else {
                instaResponse = new InstaVideoResponse();
                ((InstaVideoResponse) instaResponse).setVideoUrl(i.getVideos().getStandard_resolution().getUrl());
            }
            instaResponse.setImageUrl(i.getImages().getStandard_resolution().getUrl());
            instaResponse.setTags(i.getTags());
            instaResponse.setInstagramLink(i.getLink());
            instaResponse.setType(i.getType());
            instaResponseList.add(instaResponse);
        });
        return instaResponseList;
    }

    public ResponseEntity<Object> seeker(String searchTerm){
        String searchUrl = "https://api.instagram.com/v1/tags/"+searchTerm+"/media/recent?access_token="+AUTH_TOKEN;
        RestTemplate restTemplate = new RestTemplate();
        InstagramUrl instagramUrl = restTemplate.getForObject(searchUrl, InstagramUrl.class);
        if(instagramUrl.getData().isEmpty()){
            log.info("No result for search term ={}", searchTerm);

            return ResponseEntity.badRequest().body(builder.buildAlertResponse("no_result."));
        }
        else {
            log.info("Returning results for search term ={}", searchTerm);

            return ResponseEntity.ok(buildResponse(instagramUrl.getData()));
        }
    }
}
