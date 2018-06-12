package ucab.ingsw.socialnetworkproject.service.dataContainer.instagram;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class Videos implements Serializable {
    private Resolution standard_resolution;
}
