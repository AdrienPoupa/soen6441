package actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.util.Timeout;
import play.libs.akka.InjectedActorSupport;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

import static akka.pattern.PatternsCS.ask;
import static akka.pattern.PatternsCS.pipe;

public class UserParentActor extends AbstractActor implements InjectedActorSupport {

    private final Timeout timeout = new Timeout(2, TimeUnit.SECONDS);
    private final Set<String> defaultSearchResults;

    public static class Create {
        final String id;

        public Create(String id) {
            this.id = id;
        }
    }

    private final UserActor.Factory childFactory;

    @Inject
    public UserParentActor(UserActor.Factory childFactory) {
        System.out.println("in UserParentActor constructor");
        this.childFactory = childFactory;
        this.defaultSearchResults = new HashSet<>();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(UserParentActor.Create.class, create -> {
                    System.out.println("in UserParentActor createReceive");
                    System.out.println("create.id="+create.id);
                    ActorRef child = injectedChild(() -> childFactory.create(create.id), "userActor-" + create.id);
                    System.out.println("child="+child);
                    CompletionStage<Object> future = ask(child, new Messages.WatchSearchResults(defaultSearchResults), timeout);
                    System.out.println("ask child");
                    pipe(future, context().dispatcher()).to(sender());
                }).build();
    }

}
