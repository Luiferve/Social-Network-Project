package instagram;

import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import org.springframework.http.ResponseEntity;
import ucab.ingsw.socialnetworkproject.service.Builder;
import ucab.ingsw.socialnetworkproject.service.SearchService;
import ucab.ingsw.socialnetworkproject.service.strategy.InstagramSearchStrategy;


@RunWith(MockitoJUnitRunner.class)
public class SearchServiceTest {
    private String validSearchTerm;
    private String invalidSearchTerm;
    private String searchStrategy;
    private InstagramSearchStrategy instagramSearchStrategy;
    private SearchService searchService;


    @Before
    public void before(){
        validSearchTerm = "Caracas";
        invalidSearchTerm = "Caracas!~";
        searchStrategy = "instagram";
        instagramSearchStrategy = mock(InstagramSearchStrategy.class);
        searchService = new SearchService();
        searchService.setSearchStrategy(instagramSearchStrategy);
        searchService.setBuilder(new Builder());
    }

    @After
    public void after(){
        instagramSearchStrategy = null;
        validSearchTerm = null;
        invalidSearchTerm = null;
        searchService = null;
    }

    @Test
    public void searchServiceTest(){
        try {
            when(instagramSearchStrategy.seeker(validSearchTerm)).thenReturn(ResponseEntity.ok("success"));
            ResponseEntity<Object> response = searchService.search(validSearchTerm, searchStrategy);
            assertEquals(response.toString(), 200, response.getStatusCode().value());
        }catch(Throwable e){
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void invalidSearchTermTest(){
        try {
            ResponseEntity<Object> response = searchService.search(invalidSearchTerm, searchStrategy);
            assertEquals(response.toString(), 400, response.getStatusCode().value());
        }catch(Throwable e){
            Assert.fail(e.getMessage());
        }
    }
}
