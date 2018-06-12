package ucab.ingsw.socialnetworkproject.service.dataContainer.instagram;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class InstaData implements Serializable {
    private Images images;
    //private List<String> tags;
    private String link;
    private String type;
    private Videos videos;
}
