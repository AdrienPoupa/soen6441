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
    /**
     * Setup the tests
     */
    @BeforeClass
    public static void setup() {
        system = ActorSystem.create();
    }
    
    /**
     * Shut down the system
     */
    @AfterClass
    public static void teardown() {
        TestKit.shutdownActorSystem(system);
        system = null;
    }
    
    /**
     * Initiate test app
     */
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

    	/**
    	 * Test for the tick message
    	 */
    @Test
    public void testTickMessage() {
        Set<Status> statusesSet = new HashSet<>();
        searchResultsActorSync.setStatuses(statusesSet);
        searchResultsActorSync.setQuery("concordia");
        searchResultsActorSync.setTwitterService(twitterService);
        CompletableFuture<Void> completionStage = searchResultsActorSync.tickMessage().toCompletableFuture();
        await().until(completionStage::isDone);

        assertThat(searchResultsActorSync.getStatuses().size(), is(10));

        Status status = searchResultsActorSync.getStatuses().iterator().next();
        User user = status.getUser();

        // Test the first tweet
        assertThat(status.getFullText(), is("Concórdia anuncia a saída do técnico Mauro Ovelha. Clube deve anunciar o novo treinador nas próximas horas. Confira: https://t.co/hVbWM33wIn"));
        assertThat(user.getId(), is(143865435));
        assertThat(user.getName(), is("Rodrigo Goulart"));
        assertThat(user.getScreenName(), is("goulart0rodrigo"));
        assertThat(user.getLocation(), is("Chapecó-SC"));
        assertThat(user.getDescription(), is("Editor de esportes e colunista do jornal Diário do Iguaçu e membro da Equipe Esporte Total da Rádio Chapecó."));
        assertThat(user.getFollowers(), is("3256"));
        assertThat(user.getFriends(), is("819"));
    }

    	/**
    	 * Test for the WatchSearchResults message
    	 */
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
        assertThat(status.getFullText(), is("Concórdia anuncia a saída do técnico Mauro Ovelha. Clube deve anunciar o novo treinador nas próximas horas. Confira: https://t.co/hVbWM33wIn"));
        assertThat(user.getId(), is(143865435));
        assertThat(user.getName(), is("Rodrigo Goulart"));
        assertThat(user.getScreenName(), is("goulart0rodrigo"));
        assertThat(user.getLocation(), is("Chapecó-SC"));
        assertThat(user.getDescription(), is("Editor de esportes e colunista do jornal Diário do Iguaçu e membro da Equipe Esporte Total da Rádio Chapecó."));
        assertThat(user.getFollowers(), is("3256"));
        assertThat(user.getFriends(), is("819"));
    }
    
    /**
     * Getter test for Keyword
     */
    @Test
    public void testGetKeyword() {
        searchResultsActorSync.setQuery("test");
        Assert.assertEquals("test", searchResultsActorSync.getQuery());
    }

    /**
     * Setter test for keyword
     */
    @Test
    public void testSetKeyword() {
        searchResultsActorSync.setQuery("test");
        Assert.assertEquals("test", searchResultsActorSync.getQuery());
    }
    
    /**
     * Getter test for Statuses
     */
    @Test
    public void testGetStatuses() {
        Set<Status> statusesSet = new HashSet<>();
        searchResultsActorSync.setStatuses(statusesSet);
        Assert.assertEquals(statusesSet, searchResultsActorSync.getStatuses());
    }
    
    /**
     * Setter test for Statuses
     */
    @Test
    public void testSetStatuses() {
        Set<Status> statusesSet = new HashSet<>();
        searchResultsActorSync.setStatuses(statusesSet);
        Assert.assertEquals(statusesSet, searchResultsActorSync.getStatuses());
    }
    
    /**
     * Getter test for TwitterService
     */
    @Test
    public void testGetTwitterService() {
        searchResultsActorSync.setTwitterService(twitterService);
        Assert.assertEquals(twitterService, searchResultsActorSync.getTwitterService());
    }
    
    /**
     * Setter test for TwitterService
     */
    @Test
    public void testSetTwitterService() {
        searchResultsActorSync.setTwitterService(twitterService);
        Assert.assertEquals(twitterService, searchResultsActorSync.getTwitterService());
    }
    
    /**
     * Test for tick
     */
    @Test
    public void testTickClass() {
        SearchResultsActor.Tick tick = new SearchResultsActor.Tick();
        Assert.assertEquals(tick.getClass(), SearchResultsActor.Tick.class);
    }
}
