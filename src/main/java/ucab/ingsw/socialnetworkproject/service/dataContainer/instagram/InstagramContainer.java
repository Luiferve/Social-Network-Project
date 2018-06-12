package ucab.ingsw.socialnetworkproject.service.dataContainer.instagram;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class InstagramContainer implements Serializable {
    private List<InstaData> data;
}
