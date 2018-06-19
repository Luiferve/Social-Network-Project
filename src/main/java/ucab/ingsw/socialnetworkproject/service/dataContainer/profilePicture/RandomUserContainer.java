package ucab.ingsw.socialnetworkproject.service.dataContainer.profilePicture;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class RandomUserContainer implements Serializable {
    private List<Result> results;
}
