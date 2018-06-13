package ucab.ingsw.socialnetworkproject.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class UserVideoMediaResponse extends UserMediaResponse{
    private String videoUrl;
}
