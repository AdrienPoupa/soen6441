package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

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

    private int id;

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
    public int getId() {
        return id;
    }

    /**
     * Set the tweet ID
     * @param id ID
     */
    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return "status";
    }
}
