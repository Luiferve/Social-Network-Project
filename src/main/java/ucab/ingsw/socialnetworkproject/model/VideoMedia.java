package ucab.ingsw.socialnetworkproject.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class VideoMedia extends Media implements Serializable {
    String videoUrl;
}
