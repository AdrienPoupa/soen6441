package actors;

import models.Status;

import java.util.Set;

import static java.util.Objects.requireNonNull;

/**
 * Class containing all the Messages sent/received by the actors
 * @author Adrien Poupa
 */
public final class Messages {

    /**
     * Create UserParentActor Message
     */
    public static final class UserParentActorCreate {
        public final String id;

        public UserParentActorCreate(String id) {
            this.id = requireNonNull(id);
        }

        @Override
        public String toString() {
            return "UserParentActorCreate(" + id + ")";
        }
    }

    /**
     * WatchSearchResults Message
     */
    public static final class WatchSearchResults {
        public final String query;

        public WatchSearchResults(String query) {
            this.query = requireNonNull(query);
        }

        @Override
        public String toString() {
            return "WatchSearchResults(" + query + ")";
        }
    }

    /**
     * UnwatchSearchResults Message
     */
    public static final class UnwatchSearchResults {
        public final String query;

        public UnwatchSearchResults(String query) {
            this.query = requireNonNull(query);
        }

        @Override
        public String toString() {
            return "UnwatchSearchResults(" + query + ")";
        }
    }

    /**
     * StatusesMessage Message
     */
    public static final class StatusesMessage {
        public final Set<Status> statuses;
        public final String query;

        public StatusesMessage(Set<Status> statuses, String query) {
            this.statuses = requireNonNull(statuses);
            this.query = requireNonNull(query);
        }

        @Override
        public String toString() {
            return "StatusesMessage(" + query + ")";
        }
    }

    /**
     * RegisterActor Message
     */
    public static final class RegisterActor {
        @Override
        public String toString() {
            return "RegisterActor";
        }
    }

    /**
     * Override toString for the Messages class
     * @return string "Messages"
     */
    @Override
    public String toString() {
        return "Messages";
    }
}

