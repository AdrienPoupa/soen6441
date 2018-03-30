package actors;

import actors.Messages.SearchResults;
import actors.Messages.UnwatchSearchResults;
import actors.Messages.WatchSearchResults;
import akka.Done;
import akka.NotUsed;
import akka.actor.AbstractActor;
import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Pair;
import akka.stream.KillSwitches;
import akka.stream.Materializer;
import akka.stream.UniqueKillSwitch;
import akka.stream.javadsl.*;
import akka.util.Timeout;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.assistedinject.Assisted;
import models.SearchResult;
import models.Status;
import play.libs.Json;
import play.libs.akka.InjectedActorSupport;
import scala.concurrent.duration.Duration;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

import static akka.pattern.PatternsCS.ask;

/**
 * The broker between the WebSocket and the SearchResultsActor(s).
 * The UserActor holds the connection and sends serialized
 * JSON data to the client.
 */
public class UserActor extends AbstractActor implements InjectedActorSupport {

    private final Timeout timeout = new Timeout(Duration.create(5, TimeUnit.SECONDS));

    private final LoggingAdapter logger = Logging.getLogger(getContext().system(), this);

    private final Map<String, UniqueKillSwitch> searchResultsMap = new HashMap<>();

    private final String id;
    private final ActorRef searchResultsActor;
    private final Materializer mat;

    private final Sink<JsonNode, NotUsed> hubSink;
    private final Flow<JsonNode, JsonNode, NotUsed> websocketFlow;

    @Inject
    public UserActor(@Assisted String id,
                     @Named("searchResultsActor") ActorRef searchResultsActor,
                     Materializer mat) {
        this.id = id;
        this.searchResultsActor = searchResultsActor;
        this.searchResultsActor.tell(new Messages.RegisterActor(), self());
        this.mat = mat;

        Pair<Sink<JsonNode, NotUsed>, Source<JsonNode, NotUsed>> sinkSourcePair =
                MergeHub.of(JsonNode.class, 16)
                .toMat(BroadcastHub.of(JsonNode.class, 256), Keep.both())
                .run(mat);

        this.hubSink = sinkSourcePair.first();
        Source<JsonNode, NotUsed> hubSource = sinkSourcePair.second();

        Sink<JsonNode, CompletionStage<Done>> jsonSink = Sink.foreach((JsonNode json) -> {
            // When the user types in a stock in the upper right corner, this is triggered,
            String query = json.findPath("query").asText();
            addSearchResults(query);
        });

        // Put the source and sink together to make a flow of hub source as output (aggregating all
        // searchResults as JSON to the browser) and the actor as the sink (receiving any JSON messages
        // from the browse), using a coupled sink and source.
        this.websocketFlow = Flow.fromSinkAndSourceCoupled(jsonSink, hubSource)
                .log("actorWebsocketFlow", logger)
                .watchTermination((n, stage) -> {
                    // Stop the searchResultsActor
                    stage.thenAccept(f -> context().stop(searchResultsActor));

                    // When the flow shuts down, make sure this actor also stops.
                    stage.thenAccept(f -> context().stop(self()));

                    return NotUsed.getInstance();
                });
    }

    /**
     * The receive block, useful if other actors want to manipulate the flow.
     */
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(WatchSearchResults.class, watchSearchResults -> {
                    logger.info("Received message WatchSearchResults {}", watchSearchResults);
                    addSearchResults(watchSearchResults.query);
                    sender().tell(websocketFlow, self());
                })
                .match(UnwatchSearchResults.class, unwatchSearchResults -> {
                    logger.info("Received message UnwatchSearchResults {}", unwatchSearchResults);
                    unwatchSearchResults(unwatchSearchResults.query);
                })
                .match(Messages.SearchResultsMessage.class, message -> {
                    logger.info("Received message SearchResultsMessage {}", message);
                    SearchResult searchResults = message.searchResult;
                    addSearchResult(searchResults);
                    sender().tell(websocketFlow, self());
                })
                .build();
    }

    /**
     * Adds several searchResults to the hub, by asking the SearchResults actor for SearchResults.
     */
    private void addSearchResults(String query) {
        // Ask the searchResultsActor for a stream containing these searchResults.
        CompletionStage<SearchResults> future = ask(searchResultsActor, new WatchSearchResults(query), timeout)
                .thenApply(SearchResults.class::cast);

        // when we get the response back, we want to turn that into a flow by creating a single
        // source and a single sink, so we merge all of the stock sources together into one by
        // pointing them to the hubSink, so we can add them dynamically even after the flow
        // has started.
        /*future.thenAccept((SearchResults newSearchResults) -> {
            newSearchResults.searchResults.forEach(searchResult -> {
                if (!searchResultsMap.containsKey(searchResult.getQuery())) {
                    addSearchResult(searchResult);
                }
            });
        });*/
    }

    /**
     * Adds a single stock to the hub.
     */
    private void addSearchResult(SearchResult searchResult) {
        logger.info("Adding search result {}", searchResult);

        List<Status> statuses = searchResult.getStatuses();

        // Do not flood everything if we have no statuses
        if (statuses == null) {
            logger.info("Statuses were null");
            return;
        }

        Source<JsonNode, NotUsed> getSource = Source.from(statuses)
                .map(Json::toJson);

        // Set up a flow that will let us pull out a killswitch for this specific stock,
        // and automatic cleanup for very slow subscribers (where the browser has crashed, etc).
        final Flow<JsonNode, JsonNode, UniqueKillSwitch> killswitchFlow = Flow.of(JsonNode.class)
                .joinMat(KillSwitches.singleBidi(), Keep.right());
        // Set up a complete runnable graph from the stock source to the hub's sink
        String name = "searchresult-" + searchResult.getQuery();
        final RunnableGraph<UniqueKillSwitch> graph = getSource
                .viaMat(killswitchFlow, Keep.right())
                .to(hubSink)
                .named(name);

        // Start it up!
        UniqueKillSwitch killSwitch = graph.run(mat);

        // Pull out the kill switch so we can stop it when we want to unwatch a stock.
        searchResultsMap.put(searchResult.getQuery(), killSwitch);
    }

    private void unwatchSearchResults(String query) {
        searchResultsMap.get(query).shutdown();
        searchResultsMap.remove(query);
    }

    public interface Factory {
        Actor create(String id);
    }
}
