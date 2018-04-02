import models.Status;
import models.User;
import org.junit.*;
import play.inject.Injector;
import play.inject.guice.GuiceInjectorBuilder;
import play.mvc.Http;
import play.twirl.api.Content;
import services.TwitterApi;
import services.TwitterService;
import views.html.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static play.inject.Bindings.bind;

/**
 * TwitterController test class
 * @author Adrien Poupa
 */
public class TwitterControllerTest {

    private static TwitterService twitterService;

    private static Injector testApp;

    @BeforeClass
    public static void initTestApp() {
        testApp = new GuiceInjectorBuilder()
                .overrides(bind(TwitterApi.class).to(TwitterTestImplementation.class))
                .build();
        twitterService = testApp.instanceOf(TwitterService.class);
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
    public void testSearch() {
        Content html = search.render();
        assertThat("text/html", is(html.contentType()));
        assertThat(html.body(), containsString("Search on Twitter"));
    }

    /**
     * Test the display of the search form
     */
    @Test
    public void testProfile() throws ExecutionException, InterruptedException {
        List<Status> statuses = twitterService.getProfile("concordia")
                .toCompletableFuture().get();

        User user = statuses.get(0).getUser();

        Content html = profile.render(statuses, user);

        assertThat("text/html", is(html.contentType()));

        assertThat(html.body(), containsString("<ul>\n" +
                "        <li>User name: Concordia</li>\n" +
                "        <li>Real name: Concordia University</li>\n" +
                "        <li>Location: Montreal</li>\n" +
                "        <li>Description: Located in the vibrant and cosmopolitan city of #Montreal, #Concordia University is one of Canada’s most innovative and diverse, comprehensive universities.</li>\n" +
                "        <li>Followers: 68001</li>\n" +
                "        <li>Friends: 1191</li>\n" +
                "    </ul>"));
    }
}