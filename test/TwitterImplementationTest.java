import org.junit.*;
import play.inject.Injector;
import play.inject.guice.GuiceInjectorBuilder;
import play.libs.ws.WSResponse;
import services.TwitterApi;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;

import static play.inject.Bindings.bind;

/**
 * Twitter api test by bind TwitterApi to TwitterImplementation, and get the instance of TwitterApi
 * We use an instance of TwitterTestImplementation provided by Guice
 * @author Adrien Poupa
 */

public class TwitterImplementationTest {

    private static TwitterApi twitterTestImplementation;

    private static Injector testApp;

    /**
     * Initialise the test application, bind the TwitterAPI interface to its mock implementation
     */
    @BeforeClass
    public static void initTestApp() {
        testApp = new GuiceInjectorBuilder()
                .overrides(bind(TwitterApi.class).to(TwitterTestImplementation.class))
                .build();
        twitterTestImplementation = testApp.instanceOf(TwitterApi.class);
    }

    /**
     * Test the search for a keyword comparing the static jsonfile we expect and what is returned
     * by the mock implementation
     * @throws ExecutionException if an error occurs during execution
     * @throws InterruptedException if the request is interrupted
     * @throws IOException if we have trouble reading the static file
     */
    @Test
    public void testSearch() throws ExecutionException, InterruptedException, IOException {
        WSResponse search = twitterTestImplementation.search("test").toCompletableFuture().get();
        String body = search.getBody();

        String searchJsonFile = getJsonFileAsString("/test/resources/search.json");

        Assert.assertEquals(searchJsonFile, body);
    }

    /**
     * Test the search for a user comparing the static jsonfile we expect and what is returned
     * by the mock implementation
     * @throws ExecutionException if an error occurs during execution
     * @throws InterruptedException if the request is interrupted
     * @throws IOException if we have trouble reading the static file
     */
    @Test
    public void testProfile() throws ExecutionException, InterruptedException, IOException {
        WSResponse search = twitterTestImplementation.profile("test").toCompletableFuture().get();
        String body = search.getBody();

        String profileJsonFile = getJsonFileAsString("/test/resources/profile.json");

        Assert.assertEquals(profileJsonFile, body);
    }

    /**
     * Get the content of the mock files we store in the resources folder
     * @param path String path of the files
     * @return the String content of the file
     * @throws IOException if we did not manage to read the file
     */
    private String getJsonFileAsString(String path) throws IOException {
        String filePath = new File("").getAbsolutePath();
        byte[] encoded = Files.readAllBytes(Paths.get(filePath.concat(path)));

        return new String(encoded, Charset.defaultCharset());
    }
}
