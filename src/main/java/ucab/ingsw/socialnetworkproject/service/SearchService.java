package ucab.ingsw.socialnetworkproject.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ucab.ingsw.socialnetworkproject.service.strategy.SearchStrategy;

@Service("searchService")
public class SearchService {

    private SearchStrategy strategy;

    public void setSearchStrategy(SearchStrategy strategy){
        this.strategy = strategy;
    }

    public ResponseEntity<Object> search(String searhTerm){
        return  strategy.seeker(searhTerm);
    }
}
