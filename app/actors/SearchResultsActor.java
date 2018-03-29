package actors;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.SearchResult;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import javax.inject.Inject;
import play.libs.ws.WSClient;
import play.mvc.Result;
import services.TwitterService;

import static play.mvc.Results.ok;

/**
 * This actor contains a set of searchResults internally that may be used by
 * all websocket clients.
 */
public class SearchResultsActor extends AbstractActor {

    private final Map<String, SearchResult> searchResultMap = new HashMap<>();

    private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    private final WSClient ws;

    private final TwitterService twitterService;

    @Inject
    public SearchResultsActor(WSClient ws, TwitterService twitterService) {
        this.ws = ws;
        this.twitterService = twitterService;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Messages.WatchSearchResults.class, watchSearchResults -> {
                    log.info("in SearchResultsActor createReceive");
                    Set<SearchResult> searchResults = watchSearchResults.queries.stream()
                            // Here we have to create a new SearchResult after getting the result from Twitter
                            .map(query -> searchResultMap.compute(query, (k, v) -> {
                                try {
                                    return twitterService.getTweets(k).toCompletableFuture().get();
                                } catch (InterruptedException | ExecutionException e) {
                                    e.printStackTrace();
                                    return null;
                                }
                            }))
                            .collect(Collectors.toSet());
                    sender().tell(new Messages.SearchResults(searchResults), self());
                }).build();
    }
}
