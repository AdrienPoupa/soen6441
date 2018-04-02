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

public class TwitterService {

    @Inject
    private TwitterApi twitterImplementation;

    private ObjectMapper mapper;

    public TwitterService() {
        mapper = new ObjectMapper();
    }

    public CompletionStage<SearchResult> getTweets(final String keywords) {
            return twitterImplementation.search(keywords)
                    .thenApplyAsync(WSResponse::asJson)
                    .thenApplyAsync(this::parseTweets);
    }

    public SearchResult parseTweets(JsonNode result) {
        try {
            return mapper.treeToValue(result,
                    SearchResult.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public CompletionStage<List<Status>> getProfile(final String username) {
        return twitterImplementation.profile(username)
                    .thenApplyAsync(WSResponse::asJson)
                    .thenApplyAsync(this::parseStatuses);
    }

    public List<Status> parseStatuses(JsonNode result) {
        try {
            // We have a list of Status, so we use Status[]
            return Arrays.asList(mapper.treeToValue(result,
                    Status[].class));
        } catch (Exception e) {
            return null;
        }
    }

    public void setMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }
}
