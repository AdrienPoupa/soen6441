import org.junit.Before;
import org.junit.Test;
import play.mvc.Http;
import play.twirl.api.Content;
import views.html.index;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class HomeControllerTest {

    @Before
    public void setUp() {
        Http.Context context = mock(Http.Context.class);
        Http.Context.current.set(context);
    }

    /**
     * Test the display of the index
     */
    @Test
    public void testIndex() {
        Content html = index.render();
        assertThat("text/html", is(html.contentType()));
        assertThat(html.body(), containsString("Welcome to the project of LabK-Group3."));
    }
}
