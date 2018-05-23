package ucab.ingsw.socialnetworkproject.model;

import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import ucab.ingsw.socialnetworkproject.model.Media;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@ToString
public class Album  implements Serializable {
    @Id
    private long album_id;

    private long user_id;
    private String name;
    private String description;
    private Media[] links;
}
