package actors;

import models.SearchResult;

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
    }

    public static class SearchResultsMessage {
        public final SearchResult searchResult;
        public SearchResultsMessage(SearchResult searchResult) {
            this.searchResult = searchResult;
        }
    }

    public static class RegisterActor {
    }
}

