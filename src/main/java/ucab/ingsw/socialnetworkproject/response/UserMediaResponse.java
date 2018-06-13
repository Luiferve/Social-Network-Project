package ucab.ingsw.socialnetworkproject.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UserMediaResponse {
    private long id;
    private  String type;
    private String url;
    private String link;
}
