package actors;

import models.SearchResult;

import java.util.Set;

import static java.util.Objects.requireNonNull;

public final class Messages {

    public static final class WatchSearchResults {
        final Set<String> queries;

        public WatchSearchResults(Set<String> queries) {
            this.queries = requireNonNull(queries);
            System.out.println("creating WatchSearchResults:"+queries.toString());
        }

        @Override
        public String toString() {
            return "WatchSearchResults(" + queries.toString() + ")";
        }
    }

    public static final class UnwatchSearchResults {
        final Set<String> queries;

        public UnwatchSearchResults(Set<String> queries) {
            this.queries = requireNonNull(queries);
        }

        @Override
        public String toString() {
            return "UnwatchSearchResults(" + queries.toString() + ")";
        }
    }

    public static class SearchResults {
        final Set<SearchResult> searchResults;

        public SearchResults(Set<SearchResult> searchResults) {
            this.searchResults = requireNonNull(searchResults);
        }
    }
}

