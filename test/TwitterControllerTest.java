import controllers.TwitterController;
import org.junit.*;
import play.inject.Injector;
import play.inject.guice.GuiceApplicationBuilder;
import play.inject.guice.GuiceInjectorBuilder;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Http;
import play.mvc.Result;
import services.TwitterApi;
import services.TwitterService;
import java.util.concurrent.ExecutionException;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static play.inject.Bindings.bind;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.contentAsString;

/**
 * TwitterController test class
 * @author Adrien Poupa
 */
public class TwitterControllerTest {

    private static TwitterService twitterService;

    private static TwitterController twitterController;

    private static Injector testApp;

    /**
     * Initialise the test application
     * Set the current Http.Context
     * Get the HttpExecutionContext
     * Create a the twitterController for tests using the mock implementation of the TwitterAPI interface
     */
    @BeforeClass
    public static void initTestApp() {
        Http.Context context = mock(Http.Context.class);
        Http.Context.current.set(context);
        HttpExecutionContext ec = new GuiceApplicationBuilder().injector().instanceOf(HttpExecutionContext.class);

        testApp = new GuiceInjectorBuilder()
                .overrides(bind(TwitterApi.class).to(TwitterTestImplementation.class))
                .build();
        twitterService = testApp.instanceOf(TwitterService.class);
        twitterController = new TwitterController(twitterService, ec);
    }

    /**
     * Setup the tests.
     */
    @Before
    public void setup() {
        // Mock the context and flash to render the templates
        Http.Context context = mock(Http.Context.class);
        Http.Context.current.set(context);
    }

    /**
     * Test the display of the search form
     */
    @Test
    public void testSearch() throws ExecutionException, InterruptedException {
        Result result = twitterController.search().toCompletableFuture().get();
        assertEquals(OK, result.status());
        assertEquals("text/html", result.contentType().get());
        assertEquals("utf-8", result.charset().get());
        assertTrue(contentAsString(result).contains("Search on Twitter"));
    }

    /**
     * Test the display of the profile
     */
    @Test
    public void testProfile() throws ExecutionException, InterruptedException {
        Result result = twitterController.profile("concordia").toCompletableFuture().get();
        assertEquals(OK, result.status());
        assertEquals("text/html", result.contentType().get());
        assertEquals("utf-8", result.charset().get());

        assertThat(contentAsString(result), containsString("<ul>\n" +
                "        <li>User name: Concordia</li>\n" +
                "        <li>Real name: Concordia University</li>\n" +
                "        <li>Location: Montreal</li>\n" +
                "        <li>Description: Located in the vibrant and cosmopolitan city of #Montreal, #Concordia University is one of Canada’s most innovative and diverse, comprehensive universities.</li>\n" +
                "        <li>Followers: 68001</li>\n" +
                "        <li>Friends: 1191</li>\n" +
                "    </ul>"));
    }
}