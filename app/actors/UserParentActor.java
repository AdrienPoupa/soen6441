package actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.util.Timeout;
import play.libs.akka.InjectedActorSupport;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

import static akka.pattern.PatternsCS.ask;
import static akka.pattern.PatternsCS.pipe;

/**
 * UserParentActor for UserActor
 * Inspired by https://github.com/playframework/play-java-websocket-example/blob/2.6.x/app/actors/UserParentActor.java
 * @author Adrien Poupa
 */
public class UserParentActor extends AbstractActor implements InjectedActorSupport {

    private final Timeout timeout = new Timeout(2, TimeUnit.SECONDS);
    private final String query;

    private final UserActor.Factory childFactory;

    /**
     * Create the default UserParentActor
     * Called by the WebSocketController
     * Runs a default search on the keyword "test"
     * @param childFactory factory to create a UserActor
     */
    @Inject
    public UserParentActor(UserActor.Factory childFactory) {
        this.childFactory = childFactory;
        this.query = "test"; // default keyword
    }

    /**
     * Receive Akka messages
     * @return Receive receive
     */
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Messages.UserParentActorCreate.class, create -> {
                    ActorRef child = injectedChild(() -> childFactory.create(create.id), "userActor-" + create.id);
                    CompletionStage<Object> future = ask(child, new Messages.WatchSearchResults(query), timeout);
                    pipe(future, context().dispatcher()).to(sender());
                }).build();
    }

}
