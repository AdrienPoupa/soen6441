package actors;

import akka.actor.AbstractActorWithTimers;
import akka.actor.ActorRef;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import models.SearchResult;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.inject.Inject;
import scala.concurrent.duration.Duration;
import services.TwitterService;

/**
 * This actor contains a set of searchResults internally that may be used by
 * all websocket clients.
 */
public class SearchResultsActor extends AbstractActorWithTimers {

    private final Map<String, SearchResult> searchResultMap = new HashMap<>();

    private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    private final TwitterService twitterService;

    private List<ActorRef> userActors;

    private String keyword;

    private static final class Tick {
    }

    @Override
    public void preStart() {
        getTimers().startPeriodicTimer("Timer", new Tick(),
                Duration.create(5, TimeUnit.SECONDS));
    }

    @Inject
    public SearchResultsActor(TwitterService twitterService) {
        this.twitterService = twitterService;
        this.userActors = new ArrayList<>();
        this.keyword = null;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Messages.RegisterActor.class, msg -> userActors.add(sender()))
                .match(Tick.class, message -> {
                    // Every 5 seconds, check for new tweets
                    if (keyword != null) {
                        try {
                            log.info("tick: getTweets for keyword "+keyword);
                            SearchResult searchResults = twitterService.getTweets(keyword).toCompletableFuture().get();

                            Messages.SearchResultsMessage searchResultsMessage =
                                    new Messages.SearchResultsMessage(searchResults);
                            userActors.forEach(ar -> ar.tell(searchResultsMessage, self()));
                        } catch (InterruptedException | ExecutionException e) {
                            log.info("tick error:"+ Arrays.toString(e.getStackTrace()));
                            e.printStackTrace();
                        }
                    }
                })
                .match(Messages.WatchSearchResults.class, watchSearchResults -> {
                    Set<SearchResult> searchResults = watchSearchResults.queries.stream()
                            // Here we have to create a new SearchResult after getting the result from Twitter
                            .map(query -> searchResultMap.compute(query, (k, v) -> {
                                keyword = k;
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
