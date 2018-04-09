package services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.SearchResult;
import models.Status;
import play.libs.ws.WSResponse;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * Use the data provided by the TwitterImplementation to parse it and return
 * nice POJO instances of the tweets
 * @author Adrien Poupa
 */
public class TwitterService {

    @Inject
    private TwitterApi twitterImplementation;

    private ObjectMapper mapper;

    /**
     * Default constructor
     */
    public TwitterService() {
        mapper = new ObjectMapper();
    }

    /**
     * Parse the tweets for a keyword
     * @param keywords keyword
     * @return CompletionStage of a SearchResult
     */
    public CompletionStage<SearchResult> getTweets(final String keywords) {
            return twitterImplementation.search(keywords)
                    .thenApplyAsync(WSResponse::asJson)
                    .thenApplyAsync(this::parseTweets);
    }

    /**
     * Convert the tweets from a JsonNode to a SearchResult using jackson
     * @param result JsonNode jsonNode extracted from the twitterImplementation
     * @return SearchResult search results as a POJO
     */
    public SearchResult parseTweets(JsonNode result) {
        try {
            return mapper.treeToValue(result,
                    SearchResult.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    /**
     * Parse the tweets for a user
     * @param username username
     * @return CompletionStage of a List of Statuses
     */
    public CompletionStage<List<Status>> getProfile(final String username) {
        return twitterImplementation.profile(username)
                    .thenApplyAsync(WSResponse::asJson)
                    .thenApplyAsync(this::parseStatuses);
    }

    /**
     * Convert the tweets from a JsonNode to a List of Statuses using jackson
     * @param result JsonNode jsonNode extracted from the twitterImplementation
     * @return List of Statuses as a POJO
     */
    public List<Status> parseStatuses(JsonNode result) {
        try {
            // We have a list of Status, so we use Status[]
            return Arrays.asList(mapper.treeToValue(result,
                    Status[].class));
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    /**
     * Set the object mapper for the tests
     * @param mapper ObjectMapper
     */
    public void setMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }
}
