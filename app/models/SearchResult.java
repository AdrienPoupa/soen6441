package models;

import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Map SearchResults to an object
 * @author Adrien Poupa
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchResult {

    protected String query;

    protected List<Status> statuses;

    private static final FiniteDuration duration = Duration.create(5, TimeUnit.SECONDS);

    public SearchResult() {
    }

    /**
     * Get statuses
     * @return List<Status> statuses
     */
    public List<Status> getStatuses() {
        return statuses;
    }

    /**
     * Get statuses
     * @return List<Status> statuses
     */
    public List<Status> getUpdatedStatuses() {
        return statuses;
    }

    /**
     * Set the statuses
     * @param statuses List<Status> statuses
     */
    public void setStatuses(List<Status> statuses) {
        this.statuses = statuses;
    }

    /**
     * Search query
     * @return String the keyword(s)
     */
    public String getQuery() {
        return query;
    }

    /**
     * Set the search query
     * @param query the keyword(s)
     */
    public void setQuery(String query) {
        this.query = query;
    }

    public Optional<Status> getUnreadStatus() {
        Optional<Status> unreadStatus = statuses.stream().filter(status -> !status.isDisplayed()).findFirst();

        unreadStatus.ifPresent(status -> status.setDisplayed(true));

        return unreadStatus;
    }
}
