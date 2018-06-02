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
public class UserNewAlbumCommand implements Serializable{
    @NotNull(message = "Se requiere direccion de email.")
    @NotEmpty(message = "Se requiere direccion de email.")
    @Size(min = validationRules.EMAIL_MIN_SIZE, message = "El email debe contener al menos de 8 caracteres.")
    @Email(message = "error.format.email")
    private String email;

    @NotNull(message = "Se requiere token de autorizacion.")
    @NotEmpty(message = "Se requiere token de autorizacion.")
    private String authToken;

    @NotNull(message = "Se requiere el nombre del album.")
    @NotEmpty(message = "Se requiere el nombre del album.")
    private String name;

    @NotNull(message = "Se requiere una descripcion del album.")
    @NotEmpty(message = "Se requiere una descripcion del album.")
    private String description;
}
