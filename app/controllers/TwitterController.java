package controllers;

import play.cache.SyncCacheApi;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import services.TwitterService;
import views.html.*;


/**
 * TwitterController
 * Implements the TweetAnalytics app using Twitter's API
 * @author Adrien Poupa
 */
public class TwitterController extends Controller {

    private SyncCacheApi cache;

    private TwitterService twitterService;

    private HttpExecutionContext httpExecutionContext;

    @Inject
    public TwitterController(TwitterService twitterService, SyncCacheApi cache, HttpExecutionContext ec) {
        this.cache = cache;
        this.httpExecutionContext = ec;
        this.twitterService = twitterService;
    }

    /**
     * Search form page (GET page)
     * Displays the form
     * @return CompletionStage<SearchResult>
     */
    public CompletionStage<Result> search() {
        return CompletableFuture.completedFuture(ok(search.render()));
    }

    /**
     * Display latest statuses of a profile
     * @param username String username of the Twitter user we want the latest statuses
     * @return CompletionStage<SearchResult>
     */
    public CompletionStage<Result> profile(String username) {
        return twitterService.getProfile(username)
                             .thenApplyAsync(result -> {
                                // Store the object in cache for 5 minutes
                                cache.set("profile." + username, result, 5*60);
                                return result;
                             })
                             .thenApplyAsync(result -> ok(profile.render(result, result.get(0).getUser())),
                                     httpExecutionContext.current());
    }

}
