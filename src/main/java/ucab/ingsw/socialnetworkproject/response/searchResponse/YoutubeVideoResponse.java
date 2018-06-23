package ucab.ingsw.socialnetworkproject.response.searchResponse;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class YoutubeVideoResponse {
    private String type;
    private String thumbnail;
    private String videoUrl;
    private String title;
}
