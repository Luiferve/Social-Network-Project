package ucab.ingsw.socialnetworkproject.model;

import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import ucab.ingsw.socialnetworkproject.model.Media;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@ToString
public class Album  implements Serializable {
    @Id
    private long id;

    private long user_id;
    private String name;
    private String description;
    private List<Long> media = new ArrayList<>();
}
