package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.Keyword;
import models.Status;
import models.SearchResult;
import play.cache.SyncCacheApi;
import play.data.Form;
import play.data.FormFactory;
import play.libs.concurrent.HttpExecutionContext;
import play.libs.oauth.OAuth;
import play.libs.oauth.OAuth.ConsumerKey;
import play.libs.oauth.OAuth.OAuthCalculator;
import play.libs.oauth.OAuth.RequestToken;
import play.libs.oauth.OAuth.ServiceInfo;
import play.libs.ws.WSClient;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import com.google.common.base.Strings;

import javax.inject.Inject;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import views.html.*;


/**
 * TwitterController
 * Implements the TweetAnalytics app using Twitter's API
 * Inspired from: https://www.playframework.com/documentation/2.6.x/JavaOAuth
 * @author Adrien Poupa
 */
public class TwitterController extends Controller {
    private static final ConsumerKey KEY = new ConsumerKey("74EXt7wCHQ3caAh9RG22zfXun", "6jU0RV2MsHw2wPpSnQJWCysUrsYDm0t8e5akHSJo49JEVwxuBb");

    private static final ServiceInfo SERVICE_INFO =
            new ServiceInfo("https://api.twitter.com/oauth/request_token",
                    "https://api.twitter.com/oauth/access_token",
                    "https://api.twitter.com/oauth/authorize",
                    KEY);

    private static final OAuth TWITTER = new OAuth(SERVICE_INFO);

    private final WSClient ws;

    private final FormFactory formFactory;

    private SyncCacheApi cache;

    private HttpExecutionContext httpExecutionContext;

    private String baseUrl = "https://api.twitter.com/1.1";

    /**
     * Constructor.
     * Play injects all the dependencies we need
     * @param ws WSClient to query the API
     * @param formFactory  FormFactory
     * @param cache SyncCacheApi
     * @param ec HttpExecutionContext
     */
    @Inject
    public TwitterController(WSClient ws, final FormFactory formFactory, SyncCacheApi cache, HttpExecutionContext ec) {
        this.ws = ws;
        this.formFactory = formFactory;
        this.cache = cache;
        this.httpExecutionContext = ec;
    }

    /**
     * Search form page (GET page)
     * Displays the form
     * @return CompletionStage<SearchResult>
     */
    public CompletionStage<Result> searchForm() {
        Optional<RequestToken> sessionTokenPair = getSessionTokenPair();
        if (sessionTokenPair.isPresent()) {
            // Get the previous tweets from the cache
            List<Status> cachedStatuses = cache.get("cachedStatuses");
            // If the cache is empty, create an empty ArrayList to avoid having an exception thrown
            if (cachedStatuses == null) {
                cachedStatuses = new ArrayList<>();
            }
            return CompletableFuture.completedFuture(ok(search.render(formFactory.form(Keyword.class), cachedStatuses)));
        }
        return CompletableFuture.completedFuture(redirect(routes.TwitterController.auth()));
    }

    /**
     * Get the Json search file
     * @param keyword String
     * @param sessionTokenPair RequestToken
     * @return CompletionStage<Result>
     */
    public CompletionStage<Result> getSearchJson(String keyword, RequestToken sessionTokenPair)
    {
        return ws.url(baseUrl + "/search/tweets.json")
                .addQueryParameter("q", keyword)
                .addQueryParameter("count", "10")
                .addQueryParameter("result_type", "recent")
                .addQueryParameter("tweet_mode", "extended")
                .sign(new OAuthCalculator(TwitterController.KEY, sessionTokenPair))
                .get() // THIS IS NOT BLOCKING! It returns a promise to the response. It comes from WSRequest.
                .thenApplyAsync(result -> {
                    JsonNode rootNode = result.asJson();
                    try {
                        // Map the json result to an actual object with Jackson
                        ObjectMapper mapper = new ObjectMapper();
                        SearchResult root = mapper.treeToValue(rootNode,
                                SearchResult.class);

                        // Get the current status cache
                        List<Status> cachedStatuses = cache.get("cachedStatuses");

                        // Add statuses to the cache, or initialize the variable if cache is empty
                        if (cachedStatuses != null) {
                            cachedStatuses.addAll(root.getStatuses());
                        }
                        else {
                            cachedStatuses = root.getStatuses();
                        }

                        // Update the cache with the new statuses
                        cache.set("cachedStatuses", cachedStatuses, 15*60);

                        // Redirect to the form where all the statuses will be displayed
                        return redirect(routes.TwitterController.searchForm());
                    } catch (IOException e) {
                        return ok(e.toString());
                    }
                });
    }

    /**
     * Get the Json profile file
     * @param username String
     * @param sessionTokenPair RequestToken
     * @return CompletionStage<Result>
     */
    public CompletionStage<Result> getProfileJson(String username, RequestToken sessionTokenPair)
    {
        return ws.url(baseUrl + "/statuses/user_timeline.json")
                .addQueryParameter("count", "10")
                .addQueryParameter("tweet_mode", "extended")
                .addQueryParameter("screen_name", username)
                .sign(new OAuthCalculator(TwitterController.KEY, sessionTokenPair))
                .get() // THIS IS NOT BLOCKING! It returns a promise to the response. It comes from WSRequest.
                .thenApplyAsync(result -> {
                    JsonNode rootNode = result.asJson();
                    try {
                        // Map the json result to an actual object with Jackson
                        ObjectMapper mapper = new ObjectMapper();

                        // We have a list of Status, so we use Status[]
                        List<Status> root = Arrays.asList(mapper.treeToValue(rootNode,
                                Status[].class));

                        // Store the object in cache for 5 minutes
                        cache.set("profile." + username, root, 5*60);

                        return ok(profile.render(root, root.get(0).getUser()));
                    } catch (IOException e) {
                        return ok(e.toString());
                    }
                }, httpExecutionContext.current());
    }

    /**
     * Retrieve the latest tweets given a keyword
     * @return CompletionStage<SearchResult>
     */
    public CompletionStage<Result> searchPost() {
        Optional<RequestToken> sessionTokenPair = getSessionTokenPair();
        if (sessionTokenPair.isPresent()) {
            // Get the keywordForm
            Form<Keyword> keywordForm = formFactory.form(Keyword.class).bindFromRequest();
            // Throw an error if we have no keyword
            if (keywordForm.hasErrors() || keywordForm.hasGlobalErrors()) {
                flash("error", "Please provide a keyword");
                return CompletableFuture.completedFuture(redirect(routes.TwitterController.searchForm()));
            }
            String keyword = keywordForm.get().getKeyword();
            // Query Twitter's API
            return getSearchJson(keyword, sessionTokenPair.get());
        }
        return CompletableFuture.completedFuture(redirect(routes.TwitterController.auth()));
    }

    /**
     * Display latest statuses of a profile
     * @param username String username of the Twitter user we want the latest statuses
     * @return CompletionStage<SearchResult>
     */
    public CompletionStage<Result> profile(String username) {
        Optional<RequestToken> sessionTokenPair = getSessionTokenPair();
        if (sessionTokenPair.isPresent()) {
            return getProfileJson(username, sessionTokenPair.get());
        }
        return CompletableFuture.completedFuture(redirect(routes.TwitterController.auth()));
    }

    /**
     * Login into Twitter's API using OAuth
     * @return SearchResult
     */
    public Result auth() {
        String verifier = request().getQueryString("oauth_verifier");
        if (Strings.isNullOrEmpty(verifier)) {
            String url = routes.TwitterController.auth().absoluteURL(request());
            RequestToken requestToken = TWITTER.retrieveRequestToken(url);
            saveSessionTokenPair(requestToken);
            return redirect(TWITTER.redirectUrl(requestToken.token));
        } else {
            RequestToken requestToken = getSessionTokenPair().get();
            RequestToken accessToken = TWITTER.retrieveAccessToken(requestToken, verifier);
            saveSessionTokenPair(accessToken);
            return redirect(routes.TwitterController.searchForm());
        }
    }

    /**
     * Save the session token
     * @param requestToken RequestToken
     */
    private void saveSessionTokenPair(RequestToken requestToken) {
        session("token", requestToken.token);
        session("secret", requestToken.secret);
    }

    /**
     * Get the RequestToken
     * @return Optional<RequestToken>
     */
    private Optional<RequestToken> getSessionTokenPair() {
        if (Http.Context.current().session().containsKey("token")) {
            return Optional.of(new RequestToken(session("token"), session("secret")));
        }
        return Optional.empty();
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

}
