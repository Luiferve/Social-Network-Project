package ucab.ingsw.socialnetworkproject.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UserResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
}
