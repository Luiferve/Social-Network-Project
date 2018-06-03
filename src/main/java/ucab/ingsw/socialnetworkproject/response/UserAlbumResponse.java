package ucab.ingsw.socialnetworkproject.response;

import lombok.Data;
import lombok.ToString;
import ucab.ingsw.socialnetworkproject.model.Media;


import java.util.List;

@Data
@ToString
public class UserAlbumResponse{
    private long id;
    private String name;
    private String description;
    private List<Media> links;
}
