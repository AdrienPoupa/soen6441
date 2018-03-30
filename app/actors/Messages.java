package actors;

import models.SearchResult;
import models.Status;

import java.util.Set;

/**
 * Class containing all the Messages sent/received by the actors
 * @author Adrien Poupa
 */
public final class Messages {

    public static final class WatchSearchResults {
        final String query;

        public WatchSearchResults(String query) {
            this.query = query;
        }

        @Override
        public String toString() {
            return "WatchSearchResults(" + query + ")";
        }
    }

    public static final class UnwatchSearchResults {
        final String query;

        public UnwatchSearchResults(String query) {
            this.query = query;
        }

        @Override
        public String toString() {
            return "UnwatchSearchResults(" + query + ")";
        }
    }

    public static class SearchResults {
        final SearchResult searchResults;

        public SearchResults(SearchResult searchResults) {
            this.searchResults = searchResults;
        }

        @Override
        public String toString() {
            return "SearchResults(" + searchResults.toString() + ")";
        }
    }

    public static class StatusesMessage {
        public final Set<Status> statuses;
        public final String query;

        public StatusesMessage(Set<Status> statuses, String query) {
            this.statuses = statuses;
            this.query = query;
        }

        @Override
        public String toString() {
            return "StatusesMessage(" + query + ")";
        }
    }

    public static class RegisterActor {
        @Override
        public String toString() {
            return "RegisterActor";
        }
    }
}

