package ucab.ingsw.socialnetworkproject.service.dataContainer.profilePicture;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class Picture implements Serializable {
    private String large;
}
