package ucab.ingsw.socialnetworkproject.command;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@ToString
@Data
public class UserUpdateAlbumCommand implements Serializable {
    @NotNull(message = "Se requiere id del usuario.")
    @NotEmpty(message = "Se requiere id del usuario.")
    private String userId;

    @NotNull(message = "Se requiere id del album.")
    @NotEmpty(message = "Se requiere id del album.")
    private String albumId;

    @NotNull(message = "Se requiere token de autorizacion.")
    @NotEmpty(message = "Se requiere token de autorizacion.")
    private String authToken;

    @NotNull(message = "Se requiere el nombre del album.")
    @NotEmpty(message = "Se requiere el nombre del album.")
    private String name;

    private String description;
}
