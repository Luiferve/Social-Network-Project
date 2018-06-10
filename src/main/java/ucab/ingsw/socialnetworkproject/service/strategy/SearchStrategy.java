package ucab.ingsw.socialnetworkproject.service.strategy;

import org.springframework.http.ResponseEntity;

public interface SearchStrategy {
    public ResponseEntity<Object> seeker(String searchTerm);
}
