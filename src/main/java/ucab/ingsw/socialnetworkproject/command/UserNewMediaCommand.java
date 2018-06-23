package ucab.ingsw.socialnetworkproject.command;


import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@ToString
@Data
public class UserNewMediaCommand implements Serializable {

    @NotNull(message = "Se requiere tipo.")
    @NotEmpty(message = "Se requiere tipo.")
    private String type;

    @NotNull(message = "Se requiere id de usuario.")
    @NotEmpty(message = "Se requiere id de usuario.")
    private String userId;


    @NotNull(message = "Se requiere id del album.")
    @NotEmpty(message = "Se requiere id del album.")
    private String albumId;

    @NotNull(message = "Se requiere token de autorizacion.")
    @NotEmpty(message = "Se requiere token de autorizacion.")
    private String authToken;

    @NotNull(message = "Se requiere url.")
    @NotEmpty(message = "Se requiere url.")
    private String url;
    
    private String link;

    private String videoUrl;

}
