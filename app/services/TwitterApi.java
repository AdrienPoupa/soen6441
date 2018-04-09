package services;

import play.libs.ws.WSResponse;

import java.util.concurrent.CompletionStage;

/**
 * Interface for the TwitterApi requests
 * @author Adrien Poupa
 */
public interface TwitterApi {

    CompletionStage<WSResponse> search(String keyword);

    CompletionStage<WSResponse> profile(String username);

}
