package ucab.ingsw.socialnetworkproject.response.searchResponse;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class YoutubeResponse {
    private int totalResults;
    private int resultsPerPage;
    private String nextPageToken;
    private String prevPageToken;
    private List<YoutubeVideoResponse> items;
}
