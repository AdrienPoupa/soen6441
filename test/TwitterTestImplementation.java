import org.junit.After;
import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;
import play.routing.RoutingDsl;
import play.server.Server;
import services.TwitterApi;

import java.io.IOException;
import java.util.concurrent.CompletionStage;

import static play.mvc.Results.ok;

/**
 * Mock implementation
 */
public class TwitterTestImplementation implements TwitterApi {

    private WSClient ws;

    private Server server;

    private String bearer;

    public TwitterTestImplementation() {
        bearer = "";

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
    }

    @Override
    public CompletionStage<WSResponse> search(String keyword) {
        return ws.url("/search/tweets.json")
                .addHeader("Authorization", bearer)
                .addQueryParameter("q", keyword)
                .addQueryParameter("count", "10")
                .addQueryParameter("result_type", "recent")
                .addQueryParameter("tweet_mode", "extended")
                .get(); // THIS IS NOT BLOCKING! It returns a promise to the response. It comes from WSRequest.
    }

    @Override
    public CompletionStage<WSResponse> profile(String username) {
        return ws.url("/statuses/user_timeline.json")
                .addHeader("Authorization", bearer)
                .addQueryParameter("count", "10")
                .addQueryParameter("tweet_mode", "extended")
                .addQueryParameter("screen_name", username)
                .get(); // THIS IS NOT BLOCKING! It returns a promise to the response. It comes from WSRequest.
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
