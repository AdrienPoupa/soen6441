package actors;

import akka.actor.AbstractActorWithTimers;
import akka.actor.ActorRef;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import models.SearchResult;
import models.Status;
import scala.concurrent.duration.Duration;
import services.TwitterService;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * This actor contains a set of searchResults internally that may be used by
 * all websocket clients.
 */
public class SearchResultsActor extends AbstractActorWithTimers {

    private final TwitterService twitterService;

    private ActorRef userActor;

    private String keyword;

    private final LoggingAdapter logger = Logging.getLogger(getContext().system(), this);

    private Set<Status> statuses;

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
        this.userActor = null;
        this.keyword = null;
        this.statuses = new HashSet<>();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Messages.RegisterActor.class, message -> {
                    logger.info("Registering actor {}", message);
                    userActor = sender();
                })
                .match(Tick.class, message -> {
                    logger.info("Received message Tick {}", message);
                    if (message != null && keyword != null) {
                        // Every 5 seconds, check for new tweets if we have a keyword
                        try {
                            SearchResult searchResults = twitterService.getTweets(keyword).toCompletableFuture().get();

                            // Copy the current state of statuses in a temporary variable
                            Set<Status> oldStatuses = new HashSet<>(statuses);

                            // Add all the statuses to the list, now filtered to only add the new ones
                            statuses.addAll(searchResults.getStatuses());

                            // Copy the current state of statuses after addition in a temporary variable
                            Set<Status> newStatuses = new HashSet<>(statuses);

                            // Get the new statuses only by doing new - old = what we have to display
                            newStatuses.removeAll(oldStatuses);

                            Messages.StatusesMessage statusesMessage =
                                    new Messages.StatusesMessage(newStatuses, keyword);

                            userActor.tell(statusesMessage, self());
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .match(Messages.WatchSearchResults.class, message -> {
                    logger.info("Received message WatchSearchResults {}", message);

                    if (message != null && message.query != null) {
                        // Set the keyword
                        keyword = message.query;

                        try {
                            SearchResult searchResults = twitterService.getTweets(keyword).toCompletableFuture().get();

                            // This is the first time we want to watch a (new) keyword: reset the list
                            this.statuses = new HashSet<>();

                            // Add all the statuses to the list
                            statuses.addAll(searchResults.getStatuses());

                            Messages.StatusesMessage statusesMessage =
                                    new Messages.StatusesMessage(statuses, keyword);

                            userActor.tell(statusesMessage, self());
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                }).build();
    }
}
