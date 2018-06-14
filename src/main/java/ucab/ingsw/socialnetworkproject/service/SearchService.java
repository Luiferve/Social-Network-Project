package ucab.ingsw.socialnetworkproject.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ucab.ingsw.socialnetworkproject.response.MessageConstants;
import ucab.ingsw.socialnetworkproject.service.strategy.SearchStrategy;

@Slf4j

@Service("searchService")
public class SearchService {

    @Autowired
    private Builder builder;

    private SearchStrategy strategy;

    public void setSearchStrategy(SearchStrategy strategy){
        this.strategy = strategy;
    }

    public ResponseEntity<Object> search(String searchTerm){
        if(searchTerm.matches("[a-zA-Z0-9]*"))
            return  strategy.seeker(searchTerm);
        else
            log.info("Search term ={} contains invalid characters", searchTerm);
            return ResponseEntity.badRequest().body(builder.buildAlertResponse(MessageConstants.INVALID_SEARCH_TERM));
    }
}
