package models.twitter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Map SearchResults to an object
 * @author Adrien Poupa
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchResult {

    protected String query;

    protected List<Status> statuses;

    /**
     * Get statuses
     * @return List<Status> statuses
     */
    public List<Status> getStatuses() {
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
}
