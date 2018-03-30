package controllers;

import models.Keyword;
import models.SearchResult;
import models.Status;
import play.cache.SyncCacheApi;
import play.data.Form;
import play.data.FormFactory;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.*;
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

    private final FormFactory formFactory;

    private SyncCacheApi cache;

    private TwitterService twitterService;

    private HttpExecutionContext httpExecutionContext;

    @Inject
    public TwitterController(TwitterService twitterService, final FormFactory formFactory, SyncCacheApi cache, HttpExecutionContext ec) {
        this.formFactory = formFactory;
        this.cache = cache;
        this.httpExecutionContext = ec;
        this.twitterService = twitterService;
    }

    /**
     * Show the index
     * @return CompletionStage<Result> page
     */
    public CompletionStage<Result> index() {
        return CompletableFuture.completedFuture(ok(index.render()));
    }

    /**
     * Search form page (GET page)
     * Displays the form
     * @return CompletionStage<SearchResult>
     */
    public CompletionStage<Result> searchForm() {
        // Get the previous tweets from the cache
        List<Status> cachedStatuses = cache.get("cachedStatuses");
        // If the cache is empty, create an empty ArrayList to avoid having an exception being thrown
        if (cachedStatuses == null) {
            cachedStatuses = new ArrayList<>();
        }
        return CompletableFuture.completedFuture(ok(search.render(formFactory.form(Keyword.class), cachedStatuses)));
    }

    /**
     * Retrieve the latest tweets given a keyword
     * @return CompletionStage<SearchResult>
     */
    public CompletionStage<Result> searchPost() {
        // Get the keywordForm
        Form<Keyword> keywordForm = formFactory.form(Keyword.class).bindFromRequest();
        // Throw an error if we have no keyword
        if (keywordForm.hasErrors() || keywordForm.hasGlobalErrors()) {
            flash("error", "Please provide a keyword");
            return CompletableFuture.completedFuture(redirect(routes.TwitterController.searchForm()));
        }
        String keyword = keywordForm.get().getKeyword();

        // Query Twitter's API
        return twitterService.getTweets(keyword)
                            .thenApplyAsync(SearchResult::getStatuses)
                            .thenApplyAsync(result -> {
                                // Get the current status cache
                                List<Status> cachedStatuses = cache.get("cachedStatuses");

                                // Add statuses to the cache, or initialize the variable if cache is empty
                                if (cachedStatuses != null) {
                                    cachedStatuses.addAll(result);
                                }
                                else {
                                    cachedStatuses = result;
                                }

                                // Update the cache with the new statuses
                                cache.set("cachedStatuses", cachedStatuses, 15*60);

                                return redirect(routes.TwitterController.searchForm());
                            }, httpExecutionContext.current());
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
                             .thenApplyAsync(result -> ok(profile.render(result, result.get(0).getUser())), httpExecutionContext.current());
    }

}
