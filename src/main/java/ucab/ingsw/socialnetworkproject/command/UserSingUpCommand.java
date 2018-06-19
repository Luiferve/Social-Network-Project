package ucab.ingsw.socialnetworkproject.command;

import java.io.Serializable;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.*;

//Comando que se recibe para el registro de un usuario nuevo
@ToString
@Data
public class UserSingUpCommand implements Serializable {


    @NotNull(message = "Se requiere nombre.")
    @NotEmpty(message = "Se requiere nombre.")
    @Size(max = validationRules.FIRST_LAST_NAME_MAX_SIZE, message = "El nombre no puede contener mas de 50 caracteres.")
    @Pattern(regexp = validationRules.FIRST_LAST_NAME_REGEX, message = "El nombre posee caracteres invalidos.")
    private String firstName;

    @NotNull(message = "Se requiere apellido.")
    @NotEmpty(message = "Se requiere apellido.")
    @Size(max = validationRules.FIRST_LAST_NAME_MAX_SIZE, message = "El apellido no puede contener mas de 50 caracteres.")
    @Pattern(regexp = validationRules.FIRST_LAST_NAME_REGEX, message = "El apellido posee caracteres invalidos.")
    private String lastName;

    @NotNull(message = "Se requiere direccion de email.")
    @NotEmpty(message = "Se requiere direccion de email.")
    @Size(min = validationRules.EMAIL_MIN_SIZE, message = "El email debe contener al menos de 8 caracteres.")
    @Email(message = "error.format.email")
    private String email;

    @NotNull(message = "Se requiere contraseña.")
    @NotEmpty(message = "Se requiere contraseña.")
    //@Size(min = validationRules.PASSWORD_MIN_SIZE, message = "La contraseña debe contener al menos 6 caracteres.")
    private String password;

    @NotNull(message = "Se requiere contraseña de conformacion.")
    @NotEmpty(message = "Se requiere contraseña de confrimacion.")
    //@Size(min = validationRules.PASSWORD_MIN_SIZE, message = "La contraseña de confirmacion debe contener al menos 6 caracteres.")
    private String confirmationPassword;

    @NotNull(message = "Se requiere fecha de nacimiento.")
    @NotEmpty(message = "Se requiere fecha de nacimiento.")
    private String dateOfBirth;

    @NotNull(message = "Se requiere genero de usuario.")
    @NotEmpty(message = "Se requiere genero de usuario.")
    private String gender;
}
