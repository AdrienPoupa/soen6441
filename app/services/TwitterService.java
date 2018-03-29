package services;

import com.fasterxml.jackson.databind.ObjectMapper;
import models.SearchResult;
import models.Status;
import play.libs.ws.WSResponse;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletionStage;

public class TwitterService {

    @Inject
    TwitterApi twImpl;

    public CompletionStage<SearchResult> getTweets(final String keywords) {
            return twImpl.search(keywords)
                    .thenApplyAsync(WSResponse::asJson)
                    .thenApplyAsync(result -> {
                        try {
                            // Map the json result to an actual object with Jackson
                            ObjectMapper mapper = new ObjectMapper();
                            return mapper.treeToValue(result,
                                    SearchResult.class);
                        } catch (IOException e) {
                            return null;
                        }
                    });
    }

    public CompletionStage<List<Status>> getProfile(final String username) {
        return twImpl.profile(username)
                    .thenApplyAsync(WSResponse::asJson)
                    .thenApplyAsync(result -> {
                        try {
                            // Map the json result to an actual object with Jackson
                            ObjectMapper mapper = new ObjectMapper();

                            // We have a list of Status, so we use Status[]
                            return Arrays.asList(mapper.treeToValue(result,
                                    Status[].class));
                        } catch (IOException e) {
                            return null;
                        }
                    });
    }
}
