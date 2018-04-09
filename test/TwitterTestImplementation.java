import org.junit.After;
import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;
import play.routing.RoutingDsl;
import play.server.Server;
import services.TwitterApi;
import services.TwitterImplementation;

import java.io.IOException;
import java.util.concurrent.CompletionStage;

import static play.mvc.Results.ok;

/**
 * Mock implementation of the TwitterAPI interface
 */
public class TwitterTestImplementation implements TwitterApi {

    private WSClient ws;

    private Server server;

    private TwitterImplementation twitterImplementation;

    /**
     * Constructor
     * First, we setup a server that will return our static files for a search or a profile
     * Then, we get a test instance of the WSClient, existing in Play
     * Then, we inject this instance in the real implementation: this way, the mock server
     * will respond instead of Twitter, giving us the static files
     * Finally, we override the base URL to query the local server which responds on /search and /statuses
     * without any domain name in front of it
     */
    public TwitterTestImplementation() {
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

        // Get test instance of WSClient
        ws = play.test.WSTestClient.newClient(server.httpPort());

        // Here we will use the "real" implementation but with the mock server created above
        // Therefore, we will achieve code coverage but not call the live Twitter API!
        twitterImplementation = new TwitterImplementation(ws);
        twitterImplementation.setBaseUrl("");
    }

    /**
     * Test the search implementation
     * @param keyword keyword to search
     * @return CompletionStage of a WSResponse
     */
    @Override
    public CompletionStage<WSResponse> search(String keyword) {
        return twitterImplementation.search(keyword);
    }

    /**
     * Test the profile implementation
     * @param username username to search
     * @return CompletionStage of a WSResponse
     */
    @Override
    public CompletionStage<WSResponse> profile(String username) {
        return twitterImplementation.profile(username);
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
}
