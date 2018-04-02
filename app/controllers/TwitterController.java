package controllers;

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

    private TwitterService twitterService;

    private HttpExecutionContext httpExecutionContext;

    @Inject
    public TwitterController(TwitterService twitterService, HttpExecutionContext ec) {
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
                             .thenApplyAsync(result -> ok(profile.render(result, result.get(0).getUser())),
                                     httpExecutionContext.current());
    }

}
