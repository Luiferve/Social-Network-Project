package ucab.ingsw.socialnetworkproject.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;


@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserMediaResponse {
    private long id;
    private  String type;
    private String url;
    private String link;
}
