package ucab.ingsw.socialnetworkproject.command;


import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

//Comando que se recibe para el cierre de sesion
@ToString
@Data
public class UserLogoutCommand implements Serializable {

    @NotNull(message = "Se requiere id del usuario.")
    @NotEmpty(message = "Se requiere id del usuario.")
    private String id;

    @NotNull(message = "Se requiere token de autorizacion.")
    @NotEmpty(message = "Se requiere token de autorizacion.")
    private String authToken;
}
