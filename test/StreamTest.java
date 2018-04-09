import actors.SearchResultsActor;
import actors.UserActor;
import akka.Done;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Source;
import akka.testkit.TestActorRef;
import akka.testkit.javadsl.TestKit;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import play.inject.Injector;
import play.inject.guice.GuiceInjectorBuilder;
import services.TwitterApi;
import services.TwitterService;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static play.inject.Bindings.bind;

/**
 * Test the stream
 * @author Adrien Poupa
 */
public class StreamTest {
    private static ActorSystem system;

    private static Injector testApp;
    private static UserActor userActor;
    private static Materializer mat;

    /**
     * Setup the actor system
     */
    @BeforeClass
    public static void setup() {
        system = ActorSystem.create();
    }

    /**
     * Shutdown the actor system
     */
    @AfterClass
    public static void teardown() {
        TestKit.shutdownActorSystem(system);
        system = null;
    }

    /**
     * Send a request to the sink, ensure that the keyword sent is received
     * @throws InterruptedException InterruptedException
     * @throws ExecutionException ExecutionException
     * @throws TimeoutException TimeoutException
     */
    @Test
    public void testSink() throws InterruptedException, ExecutionException, TimeoutException {
        new TestKit(system) {{

            testApp = new GuiceInjectorBuilder()
                    .overrides(bind(TwitterApi.class).to(TwitterTestImplementation.class))
                    .build();

            TwitterService twitterService = testApp.instanceOf(TwitterService.class);
            mat = ActorMaterializer.create(system);

            final Props props = Props.create(UserActor.class);
            final TestActorRef<UserActor> subject = TestActorRef.create(system, props, "testA");
            userActor = subject.underlyingActor();
            userActor.setMat(mat);

            final Props propsSra = Props.create(SearchResultsActor.class);
            final TestActorRef<SearchResultsActor> subjectSra = TestActorRef.create(system, propsSra, "testB");
            final SearchResultsActor searchResultsActorSync = subjectSra.underlyingActor();
            searchResultsActorSync.setTwitterService(twitterService);

            Map<String, ActorRef> searchResultsActorsMap = new HashMap<>();
            searchResultsActorsMap.put("concordia", subjectSra);
            userActor.setSearchResultsActors(searchResultsActorsMap);
            userActor.createSink();


            final ObjectMapper mapper = new ObjectMapper();
            final ObjectNode root = mapper.createObjectNode();
            JsonNode childNode1 = mapper.createObjectNode();
            ((ObjectNode) childNode1).put("query", "concordia");
            ((ObjectNode) root).set("obj1", childNode1);

            final CompletionStage<Done> future = Source.from(root)
                    .runWith(userActor.getJsonSink(), mat);

            future.toCompletableFuture().get();

            Assert.assertEquals("concordia", userActor.getSearchResultsActors().entrySet().iterator().next().getKey());
        }};

    }
}
