package ucab.ingsw.socialnetworkproject.service.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ucab.ingsw.socialnetworkproject.response.searchResponse.InstaVideoResponse;
import ucab.ingsw.socialnetworkproject.service.Builder;
import ucab.ingsw.socialnetworkproject.service.dataContainer.instagram.InstaData;
import ucab.ingsw.socialnetworkproject.service.dataContainer.instagram.InstagramContainer;
import ucab.ingsw.socialnetworkproject.response.searchResponse.InstaResponse;
import java.util.ArrayList;
import java.util.List;

@Slf4j

public class InstagramSearchStrategy implements SearchStrategy {

    private static final String AUTH_TOKEN = "705751369.9ec8a89.ee214d6054f9473bb022043178680802";
    private static final String INST_IMAGE_TERM = "image";

    private List<InstaResponse> buildResponse(List<InstaData> instaData){
        List<InstaResponse> instaResponseList = new ArrayList<>();
        instaData.forEach(i -> {
            InstaResponse instaResponse;
            if(i.getType().equals(INST_IMAGE_TERM))
                instaResponse = new InstaResponse();
            else {
                instaResponse = new InstaVideoResponse();
                ((InstaVideoResponse) instaResponse).setVideoUrl(i.getVideos().getStandard_resolution().getUrl());
            }
            instaResponse.setImageUrl(i.getImages().getStandard_resolution().getUrl());
            instaResponse.setInstagramLink(i.getLink());
            instaResponse.setType(i.getType());
            instaResponseList.add(instaResponse);
        });
        return instaResponseList;
    }

    public ResponseEntity<Object> seeker(String searchTerm){
        Builder builder = new Builder();
        String searchUrl = "https://api.instagram.com/v1/tags/"+searchTerm+"/media/recent?access_token="+AUTH_TOKEN;
        RestTemplate restTemplate = new RestTemplate();
        InstagramContainer instagramContainer = restTemplate.getForObject(searchUrl, InstagramContainer.class);
        if(instagramContainer.getData().isEmpty()){
            log.info("No result for search term ={}", searchTerm);

            return ResponseEntity.badRequest().body(builder.buildAlertResponse("no_result_found"));
        }
        else {
            log.info("Returning results for search term ={}", searchTerm);

            return ResponseEntity.ok(buildResponse(instagramContainer.getData()));
        }
    }
}
