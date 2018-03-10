package models.twitter.search;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import models.twitter.User;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Status {

    protected User user;

    @JsonProperty("full_text")
    protected String fullText;

    public String getFullText() {
        return fullText;
    }

    public void setFullText(String fullText) {
        this.fullText = fullText;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
