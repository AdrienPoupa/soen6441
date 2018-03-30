package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * A Status returned from Twitter's Profile API
 * @author Adrien Poupa
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Status {

    // The API returns full_text but Jackson wants to use camelCase
    @JsonProperty("full_text")
    private String fullText;

    private User user;

    private String id;

    public Status() {
    }

    /**
     * Get the user
     * @return User
     */
    public User getUser() {
        return user;
    }

    /**
     * Set the user
     * @param user User
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Get the tweet content
     * @return String
     */
    public String getFullText() {
        return fullText;
    }

    /**
     * Set the tweet content
     * @param fullText text
     */
    public void setFullText(String fullText) {
        this.fullText = fullText;
    }

    /**
     * Get the tweet ID
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     * Set the tweet ID
     * @param id ID
     */
    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return "status";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Status status = (Status) o;
        return id.equals(status.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
