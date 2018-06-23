package ucab.ingsw.socialnetworkproject.response.searchResponse;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class SpotifyResponse {
    private String nextPageUrl;
    private String prevPageUrl;
    private List<SpotifyTrackResponse> tracks;
}
