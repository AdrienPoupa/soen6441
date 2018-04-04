package actors;

import akka.actor.Actor;
import akka.actor.IndirectActorProducer;
import com.google.inject.Injector;

/**
 * Create an actor that supports Guice dependency injection
 * Credits: https://stackoverflow.com/a/17684483
 * @author Adrien Poupa
 */
public class GuiceInjectedActor implements IndirectActorProducer {

    private final Injector injector;
    private final Class<? extends Actor> actorClass;

    /**
     * Constructor
     * @param injector Guice injector
     * @param actorClass actor to be created
     */
    public GuiceInjectedActor(Injector injector, Class<? extends Actor> actorClass) {
        this.injector = injector;
        this.actorClass = actorClass;
    }

    /**
     * Get the actor class
     * @return actor class
     */
    @Override
    public Class<? extends Actor> actorClass() {
        return actorClass;
    }

    /**
     * Produce the actor using Guice
     * @return Actor actor created
     */
    @Override
    public Actor produce() {
        return injector.getInstance(actorClass);
    }
}