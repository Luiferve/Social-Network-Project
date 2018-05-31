package ucab.ingsw.socialnetworkproject.response;

import lombok.Data;
import lombok.ToString;

//Respuesta enviada para peticiones relacionadas con el perfil (peticion del equipo front-end)
@Data
@ToString
public class UserProfileResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String dateOfBirth;
    private long [] friends;
}
