package ucab.ingsw.socialnetworkproject.service.dataContainer.instagram;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;
import java.io.Serializable;

@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class InstaData implements Serializable {
    private Images images;
    private String link;
    private String type;
    private Videos videos;
}
