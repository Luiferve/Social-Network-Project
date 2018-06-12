package ucab.ingsw.socialnetworkproject.response.searchResponse;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class InstaVideoResponse extends InstaResponse {
    private String videoUrl;
}
