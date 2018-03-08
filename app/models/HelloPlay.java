package models;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class HelloPlay {
    public static CompletionStage<String> helloPlay(final String message) {
        return CompletableFuture.completedFuture(message);
    }
}
