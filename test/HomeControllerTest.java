import controllers.HomeController;
import org.junit.Before;
import org.junit.Test;
import play.mvc.Http;
import play.mvc.Result;

import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.*;

import static org.mockito.Mockito.mock;

/**
 * Test the homepage
 * @author Adrien Poupa
 */
public class HomeControllerTest {

    /**
     * Mock the Http Context before running the tests
     * This is needed to render the template
     */
    @Before
    public void setUp() {
        Http.Context context = mock(Http.Context.class);
        Http.Context.current.set(context);
    }

    /**
     * Test the display of the index
     */
    @Test
    public void testIndex() throws ExecutionException, InterruptedException {
        Result result = new HomeController().index().toCompletableFuture().get();
        assertEquals(OK, result.status());
        assertEquals("text/html", result.contentType().get());
        assertEquals("utf-8", result.charset().get());
        assertTrue(contentAsString(result).contains("Welcome to the project of LabK-Group3."));
    }
}
