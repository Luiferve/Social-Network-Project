package ucab.ingsw.socialnetworkproject.response;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class SearchResponse {
    private List<String> urls;
}
