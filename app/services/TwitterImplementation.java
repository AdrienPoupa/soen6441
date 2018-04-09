package services;

import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

/**
 * Live implementation of the TwitterAPI search
 * Here, we only perform raw queries to the TwitterAPI and pass the results.
 * No treatment is done on the data returned yet. This is what the Service is doing.
 * Can be overridden by a mock implementation for tests
 * @author Adrien Poupa
 */
public class TwitterImplementation implements TwitterApi {

    private String baseUrl = "https://api.twitter.com/1.1";

    // OAuth2 Bearer token that allows us to perform signed requests to the Twitter API
    private String bearer = "Bearer AAAAAAAAAAAAAAAAAAAAACNX4wAAAAAANxPR9wla73%2B0iOw1Ls%2BSRhWpx6k%3D090bl90Kgf0hfAIAY1VemZ8FxVelwsvqlqO7j0041j9ivKxFgz";

    private WSClient ws;

    /**
     * Constructor
     * @param ws WSClient provided by Guice
     */
    @Inject
    public TwitterImplementation(WSClient ws) {
        this.ws = ws;
    }

    /**
     * Search for tweets given a keyword
     * @param keyword keyword to search for
     * @return CompletionStage of a WSResponse. We do not apply any treatment to the response yet.
     */
    @Override
    public CompletionStage<WSResponse> search(String keyword) {
        return ws.url(baseUrl + "/search/tweets.json")
                .addHeader("Authorization", bearer)
                .addQueryParameter("q", keyword)
                .addQueryParameter("count", "10")
                .addQueryParameter("result_type", "recent")
                .addQueryParameter("tweet_mode", "extended")
                .get(); // THIS IS NOT BLOCKING! It returns a promise to the response. It comes from WSRequest.
    }

    /**
     * Search for tweets given a username
     * @param username username to search for
     * @return CompletionStage of a WSResponse. We do not apply any treatment to the response yet.
     */
    @Override
    public CompletionStage<WSResponse> profile(String username) {
        return ws.url(baseUrl + "/statuses/user_timeline.json")
                .addHeader("Authorization", bearer)
                .addQueryParameter("count", "10")
                .addQueryParameter("tweet_mode", "extended")
                .addQueryParameter("screen_name", username)
                .get(); // THIS IS NOT BLOCKING! It returns a promise to the response. It comes from WSRequest.
    }

    /**
     * Setter for the baseUrl
     * This is useful for tests, and used in the mock implementation which, ironically,
     * uses part of the live implementation without ever querying Twitter
     * @param baseUrl baseUrl. Default is Twitter live URL as defined above.
     */
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
