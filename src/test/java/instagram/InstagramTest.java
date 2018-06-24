package instagram;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import org.springframework.http.ResponseEntity;
import ucab.ingsw.socialnetworkproject.service.strategy.InstagramSearchStrategy;

public class InstagramTest {

    private String validSearchTerm;
    private String invalidSearchTerm;
    private InstagramSearchStrategy searchStrategy;

    @Before
    public void before(){
        validSearchTerm = "Caracas";
        invalidSearchTerm = "Madrid";
        searchStrategy = new InstagramSearchStrategy();
    }

    @After
    public void after(){
        searchStrategy = null;
        validSearchTerm = null;
        invalidSearchTerm = null;
    }

    @Test
    public void searchInstagramTest(){
        try {
            ResponseEntity<Object> response = searchStrategy.seeker(validSearchTerm);
            assertEquals(response.toString(), 200, response.getStatusCode().value());
        }catch(Throwable e){
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void failedSearchInstagramTest(){
        try {
            ResponseEntity<Object> response = searchStrategy.seeker(invalidSearchTerm);
            assertEquals(response.toString(), 400, response.getStatusCode().value());
        }catch(Throwable e) {
            Assert.fail(e.getMessage());
        }
    }
}
