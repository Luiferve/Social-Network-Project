package ucab.ingsw.socialnetworkproject.service.dataContainer.spotify;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;


@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class SpotifyAlbum implements Serializable {
    private String name;
}
