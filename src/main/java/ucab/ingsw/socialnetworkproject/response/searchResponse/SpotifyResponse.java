package ucab.ingsw.socialnetworkproject.response.searchResponse;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class SpotifyResponse {
    private String nextPageOffset;
    private String prevPageOffset;
    private List<SpotifyTrackResponse> tracks;
}