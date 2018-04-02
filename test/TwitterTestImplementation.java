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
 * Mock implementation
 */
public class TwitterTestImplementation implements TwitterApi {

    private WSClient ws;

    private Server server;

    private TwitterImplementation twitterImplementation;

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

        twitterImplementation = new TwitterImplementation(ws);
        twitterImplementation.setBaseUrl("");
    }

    @Override
    public CompletionStage<WSResponse> search(String keyword) {
        return twitterImplementation.search(keyword);
    }

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
