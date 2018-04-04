import actors.Messages;
import actors.SearchResultsActor;
import actors.UserActor;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Flow;
import akka.testkit.TestActorRef;
import akka.testkit.javadsl.TestKit;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import play.inject.Injector;
import play.inject.guice.GuiceInjectorBuilder;
import services.TwitterApi;
import services.TwitterService;

import java.util.HashSet;

import static play.inject.Bindings.bind;

/**
 * Tests for the SearchResultsActor
 * @author Adrien Poupa
 */
public class UserActorTest {

    private static ActorSystem system;

    private static Injector testApp;
    private static UserActor userActor;

    @BeforeClass
    public static void setup() {
        system = ActorSystem.create();
        final Props props = Props.create(UserActor.class);
        final TestActorRef<UserActor> subject = TestActorRef.create(system, props, "testA");
        userActor = subject.underlyingActor();
    }

    @AfterClass
    public static void teardown() {
        TestKit.shutdownActorSystem(system);
        system = null;
    }

    @Test
    public void testUserActor() {
        new TestKit(system) {{
            testApp = new GuiceInjectorBuilder()
                    .overrides(bind(TwitterApi.class).to(TwitterTestImplementation.class))
                    .build();
            TwitterService twitterService = testApp.instanceOf(TwitterService.class);
            Materializer mat = ActorMaterializer.create(system);

            final Props props = Props.create(UserActor.class);
            final TestActorRef<UserActor> subject = TestActorRef.create(system, props, "testC");
            final UserActor userActor = subject.underlyingActor();
            userActor.setMat(mat);

            final Props propsSra = Props.create(SearchResultsActor.class);
            final TestActorRef<SearchResultsActor> subjectSra = TestActorRef.create(system, propsSra, "testB");
            final SearchResultsActor searchResultsActorSync = subjectSra.underlyingActor();
            searchResultsActorSync.setTwitterService(twitterService);

            userActor.setSearchResultsActor(subjectSra);
            userActor.createSink();

            subject.tell(new Messages.WatchSearchResults("concordia"), getRef()); // test registration
            // await the correct response
            expectMsgClass(duration("3 seconds"), Flow.class);

            subject.tell(new Messages.StatusesMessage(new HashSet<>(), "concordia"), getRef()); // test registration
            // await the correct response
            expectMsgClass(duration("3 seconds"), Flow.class);

            subject.tell(new Messages.UnwatchSearchResults("concordia"), getRef());
        }};
    }

    @Test
    public void testSetSearchResultsActor() {
        userActor.setSearchResultsActor(null);
        Assert.assertNull(userActor.getSearchResultsActor());
    }

    @Test
    public void testSetMaterializer() {
        userActor.setMat(null);
        Assert.assertNull(userActor.getMat());
    }

    @Test
    public void testSetSearchResultsMap() {
        userActor.setSearchResultsMap(null);
        Assert.assertNull(userActor.getSearchResultsMap());
    }

    @Test
    public void testSetJsonSink() {
        userActor.setJsonSink(null);
        Assert.assertNull(userActor.getJsonSink());
    }

    @Test
    public void testSetQuery() {
        userActor.setQuery("concordia");
        Assert.assertEquals("concordia", userActor.getQuery());
    }
}
