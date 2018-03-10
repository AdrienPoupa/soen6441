package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.Keyword;
import models.SearchResult;
import models.Status;
import play.cache.SyncCacheApi;
import play.data.Form;
import play.data.FormFactory;
import play.libs.oauth.OAuth;
import play.libs.oauth.OAuth.ConsumerKey;
import play.libs.oauth.OAuth.OAuthCalculator;
import play.libs.oauth.OAuth.RequestToken;
import play.libs.oauth.OAuth.ServiceInfo;
import play.libs.ws.WSClient;
import play.mvc.Controller;
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
 * Inspired from: https://www.playframework.com/documentation/2.6.x/JavaOAuth
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

    @Inject
    public TwitterController(WSClient ws, final FormFactory formFactory, SyncCacheApi cache) {
        this.ws = ws;
        this.formFactory = formFactory;
        this.cache = cache;
    }

    public CompletionStage<Result> searchForm() {
        List<Status> cachedStatuses = cache.get("cachedStatuses");
        if (cachedStatuses == null) {
            cachedStatuses = new ArrayList<>();
        }
        return CompletableFuture.completedFuture(ok(twitterform.render(formFactory.form(Keyword.class), cachedStatuses)));
    }

    public CompletionStage<Result> searchPost() {
        Optional<RequestToken> sessionTokenPair = getSessionTokenPair();
        if (sessionTokenPair.isPresent()) {
            Form<Keyword> keywordForm = formFactory.form(Keyword.class).bindFromRequest();
            if (keywordForm.hasErrors() || keywordForm.hasGlobalErrors()) {
                flash("error", "Please provide a keyword");
                return CompletableFuture.completedFuture(redirect(routes.TwitterController.searchForm()));
            }
            String keyword = keywordForm.get().getKeyword();
            return ws.url("https://api.twitter.com/1.1/search/tweets.json")
                    .addQueryParameter("q", keyword)
                    .addQueryParameter("count", "10")
                    .addQueryParameter("result_type", "recent")
                    .addQueryParameter("tweet_mode", "extended")
                    .sign(new OAuthCalculator(TwitterController.KEY, sessionTokenPair.get()))
                    .get()
                    .thenApply(result -> {
                        JsonNode rootNode = result.asJson();
                        try {
                            ObjectMapper mapper = new ObjectMapper();
                            SearchResult root = mapper.treeToValue(rootNode, SearchResult.class);

                            List<Status> cachedStatuses = cache.get("cachedStatuses");

                            if (cachedStatuses != null) {
                                cachedStatuses.addAll(root.getStatuses());
                            }
                            else {
                                cachedStatuses = root.getStatuses();
                            }

                            cache.set("cachedStatuses", cachedStatuses, 15*60);

                            return redirect(routes.TwitterController.searchForm());
                        } catch (IOException e) {
                            return ok(e.toString());
                        }
                    });
        }
        return CompletableFuture.completedFuture(redirect(routes.TwitterController.auth()));
    }

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

    private void saveSessionTokenPair(RequestToken requestToken) {
        session("token", requestToken.token);
        session("secret", requestToken.secret);
    }

    private Optional<RequestToken> getSessionTokenPair() {
        if (session().containsKey("token")) {
            return Optional.of(new RequestToken(session("token"), session("secret")));
        }
        return Optional.empty();
    }

}
