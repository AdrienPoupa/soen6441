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

public class TwitterImplementationTest {

    private static TwitterApi twitterTestImplementation;

    private static Injector testApp;

    @BeforeClass
    public static void initTestApp() {
        testApp = new GuiceInjectorBuilder()
                .overrides(bind(TwitterApi.class).to(TwitterTestImplementation.class))
                .build();
        twitterTestImplementation = testApp.instanceOf(TwitterApi.class);
    }

    @Test
    public void testSearch() throws ExecutionException, InterruptedException, IOException {
        WSResponse search = twitterTestImplementation.search("test").toCompletableFuture().get();
        String body = search.getBody();

        String searchJsonFile = getJsonFileAsString("/test/resources/search.json");

        Assert.assertEquals(searchJsonFile, body);
    }

    @Test
    public void testProfile() throws ExecutionException, InterruptedException, IOException {
        WSResponse search = twitterTestImplementation.profile("test").toCompletableFuture().get();
        String body = search.getBody();

        String profileJsonFile = getJsonFileAsString("/test/resources/profile.json");

        Assert.assertEquals(profileJsonFile, body);
    }

    private String getJsonFileAsString(String path) throws IOException {
        String filePath = new File("").getAbsolutePath();
        byte[] encoded = Files.readAllBytes(Paths.get(filePath.concat(path)));

        return new String(encoded, Charset.defaultCharset());
    }
}
