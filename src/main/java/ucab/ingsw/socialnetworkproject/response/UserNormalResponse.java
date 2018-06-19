package ucab.ingsw.socialnetworkproject.response;

import lombok.Data;
import lombok.ToString;

//Respuesta enviada para operciones normales
@Data
@ToString
public class UserNormalResponse {
    private long id;
    private String firstName;
    private String lastName;
    private String email;
    private String dateOfBirth;
    private String profilePicture;
}
