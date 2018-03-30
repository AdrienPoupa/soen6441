package actors;

import akka.actor.AbstractActorWithTimers;
import akka.actor.ActorRef;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import models.SearchResult;
import scala.concurrent.duration.Duration;
import services.TwitterService;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * This actor contains a set of searchResults internally that may be used by
 * all websocket clients.
 */
public class SearchResultsActor extends AbstractActorWithTimers {

    private final TwitterService twitterService;

    private List<ActorRef> userActors;

    private String keyword;

    private final LoggingAdapter logger = Logging.getLogger(getContext().system(), this);

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
                .match(Messages.RegisterActor.class, message -> userActors.add(sender()))
                .match(Tick.class, message -> {
                    logger.info("Received message Tick {}", message);
                    // Every 5 seconds, check for new tweets if we have a keyword
                    if (keyword != null) {
                        try {
                            SearchResult searchResults = twitterService.getTweets(keyword).toCompletableFuture().get();

                            Messages.SearchResultsMessage searchResultsMessage =
                                    new Messages.SearchResultsMessage(searchResults);

                            userActors.forEach(ar -> ar.tell(searchResultsMessage, self()));
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .match(Messages.WatchSearchResults.class, message -> {
                    logger.info("Received message WatchSearchResults {}", message);
                    keyword = message.query;
                    try {
                        SearchResult searchResults = twitterService.getTweets(message.query).toCompletableFuture().get();
                        sender().tell(new Messages.SearchResults(searchResults), self());
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }).build();
    }
}
