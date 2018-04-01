package services;

import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

/**
 * Live implementation
 */
public class TwitterImplementation implements TwitterApi {

    private String baseUrl = "https://api.twitter.com/1.1";

    private String bearer = "Bearer AAAAAAAAAAAAAAAAAAAAACNX4wAAAAAANxPR9wla73%2B0iOw1Ls%2BSRhWpx6k%3D090bl90Kgf0hfAIAY1VemZ8FxVelwsvqlqO7j0041j9ivKxFgz";

    @Inject
    private WSClient ws;

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

    @Override
    public CompletionStage<WSResponse> profile(String username) {
        return ws.url(baseUrl + "/statuses/user_timeline.json")
                .addHeader("Authorization", bearer)
                .addQueryParameter("count", "10")
                .addQueryParameter("tweet_mode", "extended")
                .addQueryParameter("screen_name", username)
                .get(); // THIS IS NOT BLOCKING! It returns a promise to the response. It comes from WSRequest.
    }
}
