package ucab.ingsw.socialnetworkproject.model;

import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import java.io.Serializable;

@Data
@ToString

public class User  implements Serializable {
    @Id
    private long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String dateOfBirth;
}
