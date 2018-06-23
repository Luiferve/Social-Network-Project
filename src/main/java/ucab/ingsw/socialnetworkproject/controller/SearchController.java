package ucab.ingsw.socialnetworkproject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import ucab.ingsw.socialnetworkproject.service.Builder;
import ucab.ingsw.socialnetworkproject.service.strategy.InstagramSearchStrategy;
import ucab.ingsw.socialnetworkproject.service.SearchService;
import ucab.ingsw.socialnetworkproject.service.strategy.SearchStrategy;
import ucab.ingsw.socialnetworkproject.service.strategy.YoutubeSearchStrategy;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin //Permite conexion desde aplicacion externa.
@RestController
@RequestMapping(value = "/search", produces = "application/json")
public class SearchController {

    @Autowired
    private SearchService searchService;

    @Autowired
    private Builder builder;

    private List<String> validStrategy;

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
    public ResponseEntity search(@PathVariable("strategy") String strategy, @RequestParam("q") String searchTerm, @Nullable @RequestParam("pageToken") String pageToken) {
        if(checkStrategy(strategy)) {
            if (strategy.toLowerCase().equals(validStrategy.get(0))) {
                searchService.setSearchStrategy(new InstagramSearchStrategy());
                searchTerm = searchTerm.replace(" ", "");
            }
            else if (strategy.toLowerCase().equals(validStrategy.get(1))){
                SearchStrategy searchStrategy = new YoutubeSearchStrategy();
                ((YoutubeSearchStrategy) searchStrategy).setPageToken(pageToken);
                searchService.setSearchStrategy(searchStrategy);
                searchTerm = searchTerm.replace(" ", "+");
            }/*
            else if (strategy.toLowerCase().equals(validStrategy.get(2))){
                //searchService.setSearchStrategy(new SoundcloudSearchStrategy());
            }*/
            //searchTerm = searchTerm.replace(" ", "");
            return searchService.search(searchTerm, strategy);
        }
        else{
            return ResponseEntity.badRequest().body(builder.buildAlertResponse("invalid_search_strategy"));
        }
    }

}