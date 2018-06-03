package ucab.ingsw.socialnetworkproject.command;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@ToString
@Data
public class UserRemoveAlbumCommand implements Serializable{
    @NotNull(message = "Se requiere id del usuario.")
    @NotEmpty(message = "Se requiere id del usuario.")
    private String userId;

    @NotNull(message = "Se requiere token de autorizacion.")
    @NotEmpty(message = "Se requiere token de autorizacion.")
    private String authToken;

    @NotNull(message = "Se requiere id del album.")
    @NotEmpty(message = "Se requiere id del album.")
    private String albumId;
}
