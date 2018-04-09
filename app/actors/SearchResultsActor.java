package actors;

import akka.actor.AbstractActorWithTimers;
import akka.actor.ActorRef;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import models.Status;
import scala.concurrent.duration.Duration;
import services.TwitterService;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

/**
 * This actor contains a set of searchResults internally that may be used by
 * all websocket clients.
 * Inspired by https://github.com/playframework/play-java-websocket-example/blob/2.6.x/app/actors/StocksActor.java
 * @author Adrien Poupa
 */
public class SearchResultsActor extends AbstractActorWithTimers {

    @Inject
    private TwitterService twitterService;

    private ActorRef userActor;

    private String query;

    private final LoggingAdapter logger = Logging.getLogger(getContext().system(), this);

    private Set<Status> statuses;

    /**
     * Dummy inner class used for the timer
     */
    public static final class Tick {
    }

    /**
     * Start the time, create a Tick every 5 seconds
     */
    @Override
    public void preStart() {
        getTimers().startPeriodicTimer("Timer", new Tick(),
                Duration.create(5, TimeUnit.SECONDS));
    }

    /**
     * Constructor
     */
    public SearchResultsActor() {
        this.userActor = null;
        this.query = null;
        this.statuses = new HashSet<>();
    }

    /**
     * Handle the incoming messages
     * @return Receive receive
     */
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Messages.RegisterActor.class, message -> {
                    logger.info("Registering actor {}", message);
                    userActor = sender();
                    getSender().tell("UserActor registered", getSelf());
                })
                .match(Tick.class, message -> {
                    logger.info("Received message Tick {}", message);
                    if (query != null) {
                        tickMessage();
                    }
                })
                .match(Messages.WatchSearchResults.class, message -> {
                    logger.info("Received message WatchSearchResults {}", message);
                    if (message != null && message.query != null) {
                        watchSearchResult(message);
                    }
                })
                .build();
    }

    /**
     * watchSearchResult message handling
     * @param message message to handle
     * @return CompletionStage of Void
     */
    public CompletionStage<Void> watchSearchResult(Messages.WatchSearchResults message) {
        // Set the query
        query = message.query;

        return twitterService.getTweets(query).thenAcceptAsync(searchResults -> {
            // This is the first time we want to watch a (new) query: reset the list
            this.statuses = new HashSet<>();

            // Add all the statuses to the list
            statuses.addAll(searchResults.getStatuses());

            statuses.forEach(status -> status.setQuery(query));

            Messages.StatusesMessage statusesMessage =
                    new Messages.StatusesMessage(statuses, query);

            userActor.tell(statusesMessage, self());
        });
    }

    /**
     * watchSearchResult message handling
     * @return CompletionStage of void
     */
    public CompletionStage<Void> tickMessage() {
        // Every 5 seconds, check for new tweets if we have a query
        return twitterService.getTweets(query).thenAcceptAsync(searchResults -> {
            // Copy the current state of statuses in a temporary variable
            Set<Status> oldStatuses = new HashSet<>(statuses);

            // Add all the statuses to the list, now filtered to only add the new ones
            statuses.addAll(searchResults.getStatuses());

            // Copy the current state of statuses after addition in a temporary variable
            Set<Status> newStatuses = new HashSet<>(statuses);

            // Get the new statuses only by doing new - old = what we have to display
            newStatuses.removeAll(oldStatuses);

            newStatuses.forEach(status -> status.setQuery(query));

            Messages.StatusesMessage statusesMessage =
                    new Messages.StatusesMessage(newStatuses, query);

            userActor.tell(statusesMessage, self());
        });
    }

    /**
     * Keyword getter
     * @return String query
     */
    public String getQuery() {
        return query;
    }

    /**
     * Setter for the query
     * @param query String query
     */
    public void setQuery(String query) {
        this.query = query;
    }

    /**
     * Setter for the statuses
     * @param statuses Set of Statuses statuses
     */
    public void setStatuses(Set<Status> statuses) {
        this.statuses = statuses;
    }

    /**
     * Statuses getter
     * @return Set of Status statuses
     */
    public Set<Status> getStatuses() {
        return statuses;
    }

    /**
     * Get Twitter Service
     * @return TwitterService twitterService
     */
    public TwitterService getTwitterService() {
        return twitterService;
    }

    /**
     * Set Twitter Service
     * @param twitterService twitterService
     */
    public void setTwitterService(TwitterService twitterService) {
        this.twitterService = twitterService;
    }
}
