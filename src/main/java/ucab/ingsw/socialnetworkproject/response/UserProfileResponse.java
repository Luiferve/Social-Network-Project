package ucab.ingsw.socialnetworkproject.response;

import lombok.Data;
import lombok.ToString;

import java.util.List;

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
    private List<Long> friends;
    private List<Long> albums;
}
