package instagram;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import ucab.ingsw.socialnetworkproject.controller.SearchController;
import ucab.ingsw.socialnetworkproject.service.Builder;
import ucab.ingsw.socialnetworkproject.service.SearchService;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SearchControllerTest {
    private String searchTerm;
    private String validSearchStrategy;
    private String invalidSearchStrategy;
    private SearchService searchService;
    private SearchController searchController;

    @Before
    public void before(){
        searchTerm = "Caracas";
        validSearchStrategy = "instagram";
        invalidSearchStrategy = "instagrm";
        searchService = mock(SearchService.class);
        searchController = new SearchController();
        searchController.setSearchService(searchService);
        searchController.setBuilder(new Builder());
    }

    @After
    public void after(){
        searchTerm = null;
        validSearchStrategy = null;
        invalidSearchStrategy = null;
        searchService = null;
        searchController  = null;
    }

    @Test
    public void searchControllerTest(){
        try {
            when(searchService.search(searchTerm,validSearchStrategy)).thenReturn(ResponseEntity.ok("success"));
            ResponseEntity response = searchController.search(validSearchStrategy, searchTerm, null);
            assertEquals(response.toString(), 200, response.getStatusCode().value());
        }catch(Throwable e){
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void invalidStrategyTest(){
        try {
            ResponseEntity response = searchController.search(invalidSearchStrategy, searchTerm, null);
            assertEquals(response.toString(), 400, response.getStatusCode().value());
        }catch(Throwable e){
            Assert.fail(e.getMessage());
        }
    }




}
