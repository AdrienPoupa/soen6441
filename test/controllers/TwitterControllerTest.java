package controllers;

import akka.util.ByteString;
import models.Keyword;
import models.twitter.Status;
import models.twitter.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import play.data.FormFactory;
import play.http.HttpEntity;
import play.inject.guice.GuiceApplicationBuilder;
import play.libs.concurrent.HttpExecutionContext;
import play.libs.oauth.OAuth;
import play.libs.ws.WSClient;
import play.mvc.Http;
import play.mvc.Result;
import play.routing.RoutingDsl;
import play.server.Server;
import play.test.WithBrowser;
import play.twirl.api.Content;
import views.html.twitter.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static play.mvc.Results.ok;
import static play.test.Helpers.*;

/**
 * TwitterController test class
 * @author Adrien Poupa
 */
public class TwitterControllerTest extends WithBrowser {

    private TwitterController client;
    private WSClient ws;
    private Server server;
    private FakeSyncCacheApi cache;
    private FormFactory formFactory;
    private HttpExecutionContext ec;

    /**
     * Setup the tests.
     */
    @Before
    public void setup() {
        // Mock the Twitter's API response
        server = Server.forRouter((components) -> RoutingDsl.fromComponents(components)
                .GET("/search/tweets.json").routeTo(() ->
                        ok().sendResource("search.json")
                )
                .GET("/statuses/user_timeline.json").routeTo(() ->
                        ok().sendResource("profile.json")
                )
                .build()
        );

        // Get instances of the cache, the form factory and the HttpExecutionContext with the injector
        cache = new FakeSyncCacheApi();
        formFactory = new GuiceApplicationBuilder().injector().instanceOf(FormFactory.class);
        ec = new GuiceApplicationBuilder().injector().instanceOf(HttpExecutionContext.class);
        ws = play.test.WSTestClient.newClient(server.httpPort());

        // Create the client, set its base URL
        client = new TwitterController(ws, formFactory, cache, ec);
        client.setBaseUrl("");

        // Mock the context and flash to render the templates
        Http.Context context = mock(Http.Context.class);
        Http.Flash flash = mock(Http.Flash.class);

        when(context.flash()).thenReturn(flash);
        Http.Context.current.set(context);
    }

    /**
     * Close the WSClient, stop the server
     * @throws IOException exception
     */
    @After
    public void tearDown() throws IOException {
        try {
            ws.close();
        }
        finally {
            server.stop();
        }
    }

    /**
     * We will go on the authentication page to check that the OAuth form is displayed
     */
    @Test
    public void auth() {
        // We have to run on port 9000 because of the callback URL
        running(testServer(9000), HTMLUNIT, browser -> {
            browser.goTo("/twitter/auth");
            browser.await().untilPage().isLoaded();
            // SOEN6441 Concordia is the name of our app
            // If it's showing, we have the Authorize an application page showing up!
            assertThat(browser.pageSource(), containsString("SOEN6441 Concordia"));
        });
    }

    /**
     * After connection, make sure that the form is showing
     */
    @Test
    public void searchForm() {
        // We have to run on port 9000 because of the callback URL
        running(testServer(9000), HTMLUNIT, browser -> {
            browser.goTo("/twitter/auth");
            browser.await().untilPage().isLoaded();

            // Use a dumb Twitter account to connect
            browser.$("#username_or_email").fill().with("soen6441");
            browser.$("#password").fill().with("S0en6441");
            browser.$("#allow").click();
            browser.await().untilPage().isLoaded();

            // We are logged in. Now the page we have should be either the app, or Twitter requesting additional information
            // but this still means that we logged in correctly
            assertTrue(browser.pageSource().contains("TweetAnalytics") || browser.pageSource().contains("Verify your identity"));
        });
    }

    /**
     * Given a static json file (we do not query Twitter API), make sure that we parse the statuses
     * @throws InterruptedException exception
     * @throws ExecutionException exception
     * @throws TimeoutException exception
     */
    @Test
    public void getSearchJson() throws InterruptedException, ExecutionException, TimeoutException {
        client.getSearchJson("concordia", new OAuth.RequestToken("token", "secret"))
                .toCompletableFuture().get(10, TimeUnit.SECONDS);

        // We should have 10 tweets
        List<Status> cachedStatuses = cache.get("cachedStatuses");
        assertThat(cachedStatuses.size(), is(10));

        Status status = cachedStatuses.get(0);
        User user = status.getUser();

        // Test the first tweet
        assertThat(status.getFullText(), is("Concórdia anuncia a saída do técnico Mauro Ovelha. Clube deve anunciar o novo treinador nas próximas horas. Confira: https://t.co/DOrxI7aIAj"));
        assertThat(user.getId(), is(543541226));
        assertThat(user.getName(), is("Portal DI Online"));
        assertThat(user.getScreenName(), is("PortalDIOnline"));
        assertThat(user.getLocation(), is("Chapecó - Santa Catarina"));
        assertThat(user.getDescription(), is("Ex Portal RedecomSC"));
        assertThat(user.getFollowers(), is("1502"));
        assertThat(user.getFriends(), is("168"));
    }

    /**
     * Test the display of the search form
     */
    @Test
    public void searchPost() {
        Content html = search.render(formFactory.form(Keyword.class), new ArrayList<>());
        assertThat("text/html", is(html.contentType()));
        assertThat(html.body(), containsString("Search on Twitter"));
    }

    /**
     * Test the HTML generated
     * @throws InterruptedException exception
     * @throws ExecutionException exception
     * @throws TimeoutException exception
     */
    @Test
    public void profile() throws InterruptedException, ExecutionException, TimeoutException {
        Result result = client.getProfileJson("Concordia", new OAuth.RequestToken("token", "secret"))
                .toCompletableFuture().get(10, TimeUnit.SECONDS);

        // Get the HTML body as string
        HttpEntity httpEntity = result.body();
        HttpEntity.Strict httpEntityStrict = (HttpEntity.Strict) httpEntity;
        ByteString body = httpEntityStrict.data();
        String stringBody = body.utf8String(); // get body as String.

        // Ensure that the first tweet is properly displayed
        assertThat(stringBody, containsString("<li><a href=\"/twitter/profile/Concordia\">Concordia</a> wrote: " +
                "What does big data look like? Check out the exhibition &#x27;The Material Turn&#x27; " +
                "by @Milieux_news&#x27;s Kelly Thompson @FofaGallery: " +
                "https://t.co/b04wWRNmPM Runs until April 13. https://t.co/ZJMV79FRLL</li>"));

        // Ensure that the profile is correctly displayed
        assertThat(stringBody, containsString("<ul>\n" +
                "        <li>User name: Concordia</li>\n" +
                "        <li>Real name: Concordia University</li>\n" +
                "        <li>Location: Montreal</li>\n" +
                "        <li>Description: Located in the vibrant and cosmopolitan city of #Montreal, #Concordia University is one of Canada’s most innovative and diverse, comprehensive universities.</li>\n" +
                "        <li>Followers: 68001</li>\n" +
                "        <li>Friends: 1191</li>\n" +
                "    </ul>"));
    }

    /**
     * Test the profile Json parsing
     * @throws InterruptedException exception
     * @throws ExecutionException exception
     * @throws TimeoutException exception
     */
    @Test
    public void getProfileJson() throws InterruptedException, ExecutionException, TimeoutException {
        client.getProfileJson("Concordia", new OAuth.RequestToken("token", "secret"))
                .toCompletableFuture().get(10, TimeUnit.SECONDS);

        List<Status> cachedStatuses = cache.get("profile.Concordia");

        // We should have 10 tweets
        assertThat(cachedStatuses.size(), is(10));

        Status status = cachedStatuses.get(0);
        User user = status.getUser();

        // Test the first tweet
        assertThat(status.getFullText(), is("What does big data look like? Check out the exhibition 'The Material Turn' by @Milieux_news's Kelly Thompson @FofaGallery: https://t.co/b04wWRNmPM Runs until April 13. https://t.co/ZJMV79FRLL"));
        assertThat(user.getId(), is(18173399));
        assertThat(user.getName(), is("Concordia University"));
        assertThat(user.getScreenName(), is("Concordia"));
        assertThat(user.getLocation(), is("Montreal"));
        assertThat(user.getDescription(), is("Located in the vibrant and cosmopolitan city of #Montreal, #Concordia University is one of Canada’s most innovative and diverse, comprehensive universities."));
        assertThat(user.getFollowers(), is("68001"));
        assertThat(user.getFriends(), is("1191"));
    }

    /**
     * Getter test for base URL
     */
    @Test
    public void testGetBaseUrl() {
        client.setBaseUrl("test");
        assertEquals("test", client.getBaseUrl());
    }

    /**
     * Setter test for base URL
     */
    @Test
    public void testSetBaseUrl() {
        client.setBaseUrl("test");
        assertEquals("test", client.getBaseUrl());
    }
}