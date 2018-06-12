package ucab.ingsw.socialnetworkproject.response.searchResponse;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class InstaResponse {
    private String type;
    private String imageUrl;
    //private List<String> tags;
    private String instagramLink;
}
