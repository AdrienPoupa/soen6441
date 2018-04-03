import actors.Messages;
import actors.SearchResultsActor;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.TestActorRef;
import akka.testkit.javadsl.TestKit;
import models.Status;
import models.User;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import play.inject.Injector;
import play.inject.guice.GuiceInjectorBuilder;
import services.TwitterApi;
import services.TwitterService;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static play.inject.Bindings.bind;

/**
 * Tests for the SearchResultsActor
 * @author Adrien Poupa
 */
public class SearchResultsActorMessagesTest {

    private static SearchResultsActor searchResultsActorSync;
    private static TwitterService twitterService;

    static ActorSystem system;

    private static Injector testApp;

    @BeforeClass
    public static void setup() {
        system = ActorSystem.create();
    }

    @AfterClass
    public static void teardown() {
        TestKit.shutdownActorSystem(system);
        system = null;
    }

    @BeforeClass
    public static void initTestApp() {
        testApp = new GuiceInjectorBuilder()
                .overrides(bind(TwitterApi.class).to(TwitterTestImplementation.class))
                .build();

        final Props props = Props.create(SearchResultsActor.class);

        final TestActorRef<SearchResultsActor> ref = TestActorRef.create(system, props, "testA");
        searchResultsActorSync = ref.underlyingActor();
        twitterService = testApp.instanceOf(TwitterService.class);
        searchResultsActorSync.setTwitterService(twitterService);
    }

    @Test
    public void testTickMessage() {
        Set<Status> statusesSet = new HashSet<>();
        searchResultsActorSync.setStatuses(statusesSet);
        searchResultsActorSync.setKeyword("concordia");
        searchResultsActorSync.setTwitterService(twitterService);
        CompletableFuture<Void> completionStage = searchResultsActorSync.tickMessage().toCompletableFuture();
        await().until(completionStage::isDone);

        assertThat(searchResultsActorSync.getStatuses().size(), is(10));

        Status status = searchResultsActorSync.getStatuses().iterator().next();
        User user = status.getUser();

        // Test the first tweet
        assertThat(status.getFullText(), is("No Concórdia só tem guria linda, plmds"));
        assertThat(user.getId(), is(631553577));
        assertThat(user.getName(), is("Vitorugo"));
        assertThat(user.getScreenName(), is("vitorfialla_"));
        assertThat(user.getLocation(), is("Porto Alegre, Brasil"));
        assertThat(user.getDescription(), is("Idfc"));
        assertThat(user.getFollowers(), is("317"));
        assertThat(user.getFriends(), is("162"));
    }

    @Test
    public void testWatchSearchResults() {
        Set<Status> statusesSet = new HashSet<>();
        searchResultsActorSync.setStatuses(statusesSet);
        Messages.WatchSearchResults watchSearchResults = new Messages.WatchSearchResults("concordia");
        CompletableFuture<Void> completionStage = searchResultsActorSync.watchSearchResult(watchSearchResults)
                .toCompletableFuture();
        await().until(completionStage::isDone);

        assertThat(searchResultsActorSync.getStatuses().size(), is(10));

        Status status = searchResultsActorSync.getStatuses().iterator().next();
        User user = status.getUser();

        // Test the first tweet
        assertThat(status.getFullText(), is("No Concórdia só tem guria linda, plmds"));
        assertThat(user.getId(), is(631553577));
        assertThat(user.getName(), is("Vitorugo"));
        assertThat(user.getScreenName(), is("vitorfialla_"));
        assertThat(user.getLocation(), is("Porto Alegre, Brasil"));
        assertThat(user.getDescription(), is("Idfc"));
        assertThat(user.getFollowers(), is("317"));
        assertThat(user.getFriends(), is("162"));
    }

    @Test
    public void testGetKeyword() {
        searchResultsActorSync.setKeyword("test");
        Assert.assertEquals("test", searchResultsActorSync.getKeyword());
    }

    @Test
    public void testSetKeyword() {
        searchResultsActorSync.setKeyword("test");
        Assert.assertEquals("test", searchResultsActorSync.getKeyword());
    }

    @Test
    public void testGetStatuses() {
        Set<Status> statusesSet = new HashSet<>();
        searchResultsActorSync.setStatuses(statusesSet);
        Assert.assertEquals(statusesSet, searchResultsActorSync.getStatuses());
    }

    @Test
    public void testSetStatuses() {
        Set<Status> statusesSet = new HashSet<>();
        searchResultsActorSync.setStatuses(statusesSet);
        Assert.assertEquals(statusesSet, searchResultsActorSync.getStatuses());
    }

    @Test
    public void testGetTwitterService() {
        searchResultsActorSync.setTwitterService(twitterService);
        Assert.assertEquals(twitterService, searchResultsActorSync.getTwitterService());
    }

    @Test
    public void testSetTwitterService() {
        searchResultsActorSync.setTwitterService(twitterService);
        Assert.assertEquals(twitterService, searchResultsActorSync.getTwitterService());
    }

    @Test
    public void testTickClass() {
        SearchResultsActor.Tick tick = new SearchResultsActor.Tick();
        Assert.assertEquals(tick.getClass(), SearchResultsActor.Tick.class);
    }
}
