package ucab.ingsw.socialnetworkproject.response.searchResponse;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InstaResponse {
    private String type;
    private String imageUrl;
    private String instagramLink;
    private String videoUrl;
}
