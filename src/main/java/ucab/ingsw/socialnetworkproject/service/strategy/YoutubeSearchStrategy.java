package ucab.ingsw.socialnetworkproject.service.strategy;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.youtube.YouTube;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import ucab.ingsw.socialnetworkproject.response.MessageConstants;
import ucab.ingsw.socialnetworkproject.response.searchResponse.YoutubeResponse;
import ucab.ingsw.socialnetworkproject.response.searchResponse.YoutubeVideoResponse;
import ucab.ingsw.socialnetworkproject.service.Builder;

import java.util.ArrayList;
import java.util.List;

@Slf4j


public class YoutubeSearchStrategy implements SearchStrategy {
    private static final long MAX_SEARCH_RESULTS = 10;
    private static final String API_KEY = "AIzaSyA0MCKiAjly2_r_b5JbDqPPvo7CzBkE3CE";
    private static final String TYPE = "video";
    private static final String ORDER = "relevance";
    private String pageToken;

    private YouTube buildYoutube(){
        return  new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(),
                (request) -> {}).setApplicationName("Social Network Project").build();
    }

    private String buildVideoUrl(String videoId){
        return "https://www.youtube.com/watch?v="+videoId;
    }

    private YoutubeResponse buildResponse(SearchListResponse searchResponse){
        List<YoutubeVideoResponse> youtubeVideoResponseList = new ArrayList<>();
        List<SearchResult> searchResultList = searchResponse.getItems();
        YoutubeResponse youtubeResponse = new YoutubeResponse();
        youtubeResponse.setNextPageToken(searchResponse.getNextPageToken());
        youtubeResponse.setPrevPageToken(searchResponse.getPrevPageToken());
        youtubeResponse.setResultsPerPage(searchResponse.getPageInfo().getResultsPerPage());
        youtubeResponse.setTotalResults(searchResponse.getPageInfo().getTotalResults());
        searchResultList.forEach(i-> {
            YoutubeVideoResponse youtubeVideoResponse = new YoutubeVideoResponse();
            youtubeVideoResponse.setType(TYPE);
            youtubeVideoResponse.setThumbnail(i.getSnippet().getThumbnails().getHigh().getUrl());
            youtubeVideoResponse.setTitle(i.getSnippet().getTitle());
            youtubeVideoResponse.setVideoUrl(buildVideoUrl(i.getId().getVideoId()));
            youtubeVideoResponseList.add(youtubeVideoResponse);
        });
        youtubeResponse.setItems(youtubeVideoResponseList);
        return youtubeResponse;
    }

    public void setPageToken(String pageToken) {
        this.pageToken = pageToken;
    }

    public ResponseEntity<Object> seeker(String searchTerm) {
       try{
           Builder builder = new Builder();
           YouTube youTube = buildYoutube();
           YouTube.Search.List search = youTube.search().list("id,snippet");
           search.setKey(API_KEY);
           search.setQ(searchTerm);
           search.setType(TYPE);
           search.setOrder(ORDER);
           search.setPageToken(pageToken);
           search.setMaxResults(MAX_SEARCH_RESULTS);
           search.setFields("nextPageToken,prevPageToken,pageInfo/totalResults,pageInfo/resultsPerPage,items(id/videoId,snippet/title,snippet/thumbnails/high/url)");
           SearchListResponse searchResponse = search.execute();
           YoutubeResponse youtubeResponse = buildResponse(searchResponse);
           if(youtubeResponse.getItems().isEmpty()){
               log.info("No result for search term ={}", searchTerm);

               return ResponseEntity.badRequest().body(builder.buildAlertResponse(MessageConstants.NO_RESULT));
           }
           else{
               log.info("Returning results for search term ={}", searchTerm);
               return ResponseEntity.ok(youtubeResponse);
           }
       } catch(GoogleJsonResponseException e){
           log.info("Error parsing Json", searchTerm);
           return ResponseEntity.badRequest().body(e.getMessage());
       } catch(Throwable t){
           return ResponseEntity.badRequest().body(t.getMessage());
       }
    }
}
