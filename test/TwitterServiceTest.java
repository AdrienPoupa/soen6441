import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.SearchResult;
import models.Status;
import models.User;
import org.junit.BeforeClass;
import org.junit.Test;
import play.inject.Injector;
import play.inject.guice.GuiceInjectorBuilder;
import services.TwitterApi;
import services.TwitterService;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static play.inject.Bindings.bind;
import static org.hamcrest.core.Is.is;

/**
 * Test the TwitterService
 * @author Adrien Poupa
 */
public class TwitterServiceTest {

    private static TwitterService twitterService;

    /**
     * Initialise the test application, bind the TwitterAPI interface to its mock implementation
     */
    @BeforeClass
    public static void initTestApp() {
        Injector testApp = new GuiceInjectorBuilder()
                .overrides(bind(TwitterApi.class).to(TwitterTestImplementation.class))
                .build();
        twitterService = testApp.instanceOf(TwitterService.class);
    }

    /**
     * Given a static json file (we do not query Twitter API), make sure that we parse the statuses
     * @throws InterruptedException exception
     * @throws ExecutionException exception
     */
    @Test
    public void testGetTweets() throws InterruptedException, ExecutionException {
        SearchResult searchResults = twitterService.getTweets("concordia")
                .toCompletableFuture().get();

        // We should have 10 tweets
        List<Status> statuses = searchResults.getStatuses();
        assertThat(statuses.size(), is(10));

        Status status = statuses.get(0);
        User user = status.getUser();

        // Test the first tweet
        assertThat(status.getFullText(), is("Concórdia anuncia a saída do técnico Mauro Ovelha. Clube deve anunciar o novo treinador nas próximas horas. Confira: https://t.co/DOrxI7aIAj"));
        assertThat(user.getId(), is(543541226));
        assertThat(user.getName(), is("Portal DI Online"));
        assertThat(user.getScreenName(), is("PortalDIOnline"));
        assertThat(user.getLocation(), is("Chapecó - Santa Catarina"));
        assertThat(user.getDescription(), is("Ex Portal RedecomSC"));
        assertThat(user.getFollowers(), is("1502"));
        assertThat(user.getFriends(), is("168"));
    }

    /**
     * Test the tweets Json parsing exception
     */
    @Test
    public void testGetTweetsException() {
        ObjectMapper om = mock(ObjectMapper.class);
        try {
            when(om.treeToValue(any(TreeNode.class), eq(SearchResult.class))).thenThrow(new JsonProcessingException("") {});
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        twitterService.setMapper(om);
        SearchResult searchResult = twitterService.parseTweets(null);
        twitterService.setMapper(new ObjectMapper());
        assertNull(searchResult);
    }

    /**
     * Test the profile Json parsing
     * @throws InterruptedException exception
     * @throws ExecutionException exception
     */
    @Test
    public void testGetProfile() throws InterruptedException, ExecutionException {
        List<Status> statuses = twitterService.getProfile("Concordia")
                .toCompletableFuture().get();

        // We should have 10 tweets
        assertThat(statuses.size(), is(10));

        Status status = statuses.get(0);
        User user = status.getUser();

        // Test the first tweet
        assertThat(status.getFullText(), is("What does big data look like? Check out the exhibition 'The Material Turn' by @Milieux_news's Kelly Thompson @FofaGallery: https://t.co/b04wWRNmPM Runs until April 13. https://t.co/ZJMV79FRLL"));
        assertThat(user.getId(), is(18173399));
        assertThat(user.getName(), is("Concordia University"));
        assertThat(user.getScreenName(), is("Concordia"));
        assertThat(user.getLocation(), is("Montreal"));
        assertThat(user.getDescription(), is("Located in the vibrant and cosmopolitan city of #Montreal, #Concordia University is one of Canada’s most innovative and diverse, comprehensive universities."));
        assertThat(user.getFollowers(), is("68001"));
        assertThat(user.getFriends(), is("1191"));
    }

    /**
     * Test the profile Json parsing exception
     */
    @Test
    public void testGetProfileException() {
        ObjectMapper om = mock(ObjectMapper.class);
        try {
            when(om.treeToValue(any(TreeNode.class), eq(Status[].class))).thenThrow(new JsonProcessingException("") {});
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        twitterService.setMapper(om);
        List<Status> statuses = twitterService.parseStatuses(null);
        twitterService.setMapper(new ObjectMapper());
        assertNull(statuses);
    }
}
