package ucab.ingsw.socialnetworkproject.response;

import lombok.Data;
import lombok.ToString;

//Respuesta enviada durante el login
@Data
@ToString
public class UserLogInResponse {
    private long id;
    private String firstName;
    private String lastName;
    private String email;
    private String dateOfBirth;
    private String authToken;
    private String profilePicture;
}
