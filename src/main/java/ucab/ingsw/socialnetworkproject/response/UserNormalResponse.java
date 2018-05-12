package ucab.ingsw.socialnetworkproject.response;

import lombok.Data;
import lombok.ToString;

//Respuesta enviada para peticiones no relacionadas con el perfil
@Data
@ToString
public class UserNormalResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String dateOfBirth;
}
