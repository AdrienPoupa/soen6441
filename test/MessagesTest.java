import actors.Messages;
import models.SearchResult;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;

/**
 * Messages tests
 * @author Adrien Poupa
 */
public class MessagesTest {

    @Test
    public void testUserParentActorCreate() {
        Messages.UserParentActorCreate userParentActorCreate = new Messages.UserParentActorCreate("1");
        Assert.assertEquals("1", userParentActorCreate.id);
        Assert.assertEquals("UserParentActorCreate(1)", userParentActorCreate.toString());
    }

    @Test
    public void testWatchSearchResults() {
        Messages.WatchSearchResults watchSearchResults = new Messages.WatchSearchResults("test");
        Assert.assertEquals("test", watchSearchResults.query);
        Assert.assertEquals("WatchSearchResults(test)", watchSearchResults.toString());
    }

    @Test
    public void testUnWatchSearchResults() {
        Messages.UnwatchSearchResults unWatchSearchResults = new Messages.UnwatchSearchResults("test");
        Assert.assertEquals("test", unWatchSearchResults.query);
        Assert.assertEquals("UnwatchSearchResults(test)", unWatchSearchResults.toString());
    }

    @Test
    public void testStatusesMessages() {
        Messages.StatusesMessage statusesMessage = new Messages.StatusesMessage(new HashSet<>(), "test");
        Assert.assertEquals("test", statusesMessage.query);
        Assert.assertEquals("StatusesMessage(test)", statusesMessage.toString());
    }

    @Test
    public void testRegisterActor() {
        Messages.RegisterActor registerActor = new Messages.RegisterActor();
        Assert.assertEquals("RegisterActor", registerActor.toString());
    }

    @Test
    public void testMessages() {
        Messages messages = new Messages();
        Assert.assertEquals("Messages", messages.toString());
    }

}
