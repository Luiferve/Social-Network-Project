package ucab.ingsw.socialnetworkproject.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UserProfileResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String dateOfBirth;
}
