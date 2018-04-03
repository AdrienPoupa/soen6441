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
 */
public class SearchResultsActor extends AbstractActorWithTimers {

    @Inject
    private TwitterService twitterService;

    private ActorRef userActor;

    private String keyword;

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
        this.keyword = null;
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
                    if (keyword != null) {
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
     */
    public CompletionStage<Void> watchSearchResult(Messages.WatchSearchResults message) {
        // Set the keyword
        keyword = message.query;

        return twitterService.getTweets(keyword).thenAcceptAsync(searchResults -> {
            // This is the first time we want to watch a (new) keyword: reset the list
            this.statuses = new HashSet<>();

            // Add all the statuses to the list
            statuses.addAll(searchResults.getStatuses());

            Messages.StatusesMessage statusesMessage =
                    new Messages.StatusesMessage(statuses, keyword);

            userActor.tell(statusesMessage, self());
        });
    }

    /**
     * watchSearchResult message handling
     */
    public CompletionStage<Void> tickMessage() {
        // Every 5 seconds, check for new tweets if we have a keyword
        return twitterService.getTweets(keyword).thenAcceptAsync(searchResults -> {
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
        });
    }

    /**
     * Keyword getter
     * @return String keyword
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * Setter for the keyword
     * @param keyword String keyword
     */
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Setter for the statuses
     * @param statuses Set<Statuses> statuses
     */
    public void setStatuses(Set<Status> statuses) {
        this.statuses = statuses;
    }

    /**
     * Statuses getter
     * @return Set<Status> statuses
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
