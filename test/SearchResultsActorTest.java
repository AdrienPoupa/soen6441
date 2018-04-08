import actors.Messages;
import actors.SearchResultsActor;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import play.inject.Injector;
import play.inject.guice.GuiceInjectorBuilder;
import services.TwitterApi;
import services.TwitterService;
import static play.inject.Bindings.bind;

import akka.actor.ActorSystem;
import akka.actor.Props;

import akka.testkit.javadsl.TestKit;
import akka.testkit.TestActorRef;

/**
 * Tests for the SearchResultsActor
 * @author Adrien Poupa
 */
public class SearchResultsActorTest {

    static ActorSystem system;

    private static Injector testApp;
    
    /**
     * Setup the tests
     */
    @BeforeClass
    public static void setup() {
        system = ActorSystem.create();
    }
    
    /**
     * Shut down system
     */
    @AfterClass
    public static void teardown() {
        TestKit.shutdownActorSystem(system);
        system = null;
    }
    
    /**
     * Test for the SearchResultsActor
     */
    @Test
    public void testSearchResultsActor() {
        new TestKit(system) {{
            testApp = new GuiceInjectorBuilder()
                    .overrides(bind(TwitterApi.class).to(TwitterTestImplementation.class))
                    .build();
            TwitterService twitterService = testApp.instanceOf(TwitterService.class);

            final Props props = Props.create(SearchResultsActor.class);
            final TestActorRef<SearchResultsActor> subject = TestActorRef.create(system, props, "testB");
            final SearchResultsActor searchResultsActorSync = subject.underlyingActor();
            searchResultsActorSync.setTwitterService(twitterService);

            subject.tell(new Messages.RegisterActor(), getRef()); // test registration
            // await the correct response
            expectMsg(duration("1 seconds"), "UserActor registered");

            // the run() method needs to finish within 3 seconds
            within(duration("3 seconds"), () -> {
                subject.tell(new Messages.WatchSearchResults("concordia"), getRef());

                // response must have been enqueued to us before probe
                expectMsgClass(duration("3 seconds"), Messages.StatusesMessage.class);
                return null;
            });

            // the run() method needs to finish within 3 seconds
            within(duration("3 seconds"), () -> {
                subject.tell(new SearchResultsActor.Tick(), getRef());

                // response must have been enqueued to us before probe
                expectMsgClass(duration("3 seconds"), Messages.StatusesMessage.class);
                return null;
            });
        }};
    }
}
