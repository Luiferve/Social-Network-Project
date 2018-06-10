package ucab.ingsw.socialnetworkproject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;
import ucab.ingsw.socialnetworkproject.response.AlertResponse;
import ucab.ingsw.socialnetworkproject.service.strategy.InstagramSearchStrategy;
import ucab.ingsw.socialnetworkproject.service.SearchService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin //Permite conexion desde aplicacion externa.
@RestController
@RequestMapping(value = "/search", produces = "application/json")
public class SearchController {

    @Autowired
    private SearchService searchService;

    private List<String> validStrategy;

    private AlertResponse buildAlertResponse(String message){
        AlertResponse response = new AlertResponse();
        response.setMessage(message);
        response.setTimestamp(LocalDateTime.now());
        return response;
    }

    private void setValidStrategy(){
        validStrategy = new ArrayList<>();
        validStrategy.add("instagram");
        validStrategy.add("youtube");
        validStrategy.add("soundcloud");
    }

    private boolean checkStrategy(String strategy){
        setValidStrategy();
        if(validStrategy.contains(strategy.toLowerCase())){
            return true;
        }
        else
            return false;
    }

    @RequestMapping(value = "/{strategy}", method = RequestMethod.GET)
    public ResponseEntity search(@PathVariable("strategy") String strategy, @RequestParam("q") String searchTerm) {
        boolean result = checkStrategy(strategy);
        if(result) {
            if (strategy.toLowerCase().equals("instagram")) {
                searchService.setSearchStrategy(new InstagramSearchStrategy());
            }
            searchTerm = searchTerm.replace(" ", "");
            return searchService.search(searchTerm);
        }
        else{
            return ResponseEntity.badRequest().body(buildAlertResponse("invalid_search_strategy"));
        }
    }

}
