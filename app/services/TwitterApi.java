package services;

import play.libs.ws.WSResponse;

import java.util.concurrent.CompletionStage;

public interface TwitterApi {

    CompletionStage<WSResponse> search(String keyword);

    CompletionStage<WSResponse> profile(String username);

}
