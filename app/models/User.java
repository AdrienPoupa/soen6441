package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A User Object to map Users returned by the Twitter API
 * @author Adrien Poupa
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

    private int id;

    private String name;

    // The API returns screen_name but Jackson wants to use camelCase
    @JsonProperty("screen_name")
    private String screenName;

    private String location;

    private String description;

    // The API returns followers_count but Jackson wants to use camelCase
    @JsonProperty("followers_count")
    private String followers;

    // The API returns friends_count but Jackson wants to use camelCase
    @JsonProperty("friends_count")
    private String friends;

    /**
     * Get the user's location
     * @return String location
     */
    public String getLocation() {
        return location;
    }

    /**
     * Set the user's location
     * @param location location
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Get the user's description
     * @return String description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the user's description
     * @param description description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get the user's followers
     * @return String followers
     */
    public String getFollowers() {
        return followers;
    }

    /**
     * Set the user's followers
     * @param followers followers
     */
    public void setFollowers(String followers) {
        this.followers = followers;
    }

    /**
     * Get the user's friends
     * @return String friends
     */
    public String getFriends() {
        return friends;
    }

    /**
     * Set the user's friends
     * @param friends friends
     */
    public void setFriends(String friends) {
        this.friends = friends;
    }

    /**
     * Get the user's ID
     * @return int ID
     */
    public int getId() {
        return id;
    }

    /**
     * Set the user's ID
     * @param id ID
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Get the user's real name
     * @return String real name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the user's real name
     * @param name real name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the user's screen name
     * @return String screen name
     */
    public String getScreenName() {
        return screenName;
    }

    /**
     * Set the user's screen name
     * @param screenName screen name
     */
    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }
}
