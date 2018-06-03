package ucab.ingsw.socialnetworkproject.command;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@ToString
@Data
public class UserRemoveMediaCommand implements Serializable {
    @NotNull(message = "Se requiere id de usuario.")
    @NotEmpty(message = "Se requiere id de usuario.")
    private String userId;


    @NotNull(message = "Se requiere id del album.")
    @NotEmpty(message = "Se requiere id del album.")
    private String albumId;

    @NotNull(message = "Se requiere id de media.")
    @NotEmpty(message = "Se requiere id de media.")
    private String mediaId;

    @NotNull(message = "Se requiere token de autorizacion.")
    @NotEmpty(message = "Se requiere token de autorizacion.")
    private String authToken;
}
