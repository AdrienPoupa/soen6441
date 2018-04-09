import play.shaded.ahc.org.asynchttpclient.AsyncHttpClient;
import play.shaded.ahc.org.asynchttpclient.BoundRequestBuilder;
import play.shaded.ahc.org.asynchttpclient.ListenableFuture;
import play.shaded.ahc.org.asynchttpclient.ws.WebSocket;
import play.shaded.ahc.org.asynchttpclient.ws.WebSocketListener;
import play.shaded.ahc.org.asynchttpclient.ws.WebSocketTextListener;
import play.shaded.ahc.org.asynchttpclient.ws.WebSocketUpgradeHandler;
import org.slf4j.Logger;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

/**
 * A quick wrapper around AHC WebSocket
 *
 * Credits: https://github.com/playframework/play-java-websocket-example/blob/2.6.x/test/controllers/WebSocketClient.java
 * https://github.com/AsyncHttpClient/async-http-client/blob/2.0/client/src/main/java/org/asynchttpclient/ws/WebSocket.java
 */
public class WebSocketClient {

    private AsyncHttpClient client;

    /**
     * Constructor
     * @param c AsyncHttpClient
     */
    public WebSocketClient(AsyncHttpClient c) {
        this.client = c;
    }
    
    /**
     * 
     * @param url
     * @param listener
     * @return CompletableFuture<WebSocket> future.toCompletableFuture
     * @throws ExecutionException exception
     * @throws InterruptedException exception
     */
    public CompletableFuture<WebSocket> call(String url, WebSocketTextListener listener) throws ExecutionException, InterruptedException {
        final BoundRequestBuilder requestBuilder = client.prepareGet(url);

        final WebSocketUpgradeHandler handler = new WebSocketUpgradeHandler.Builder().addWebSocketListener(listener).build();
        final ListenableFuture<WebSocket> future = requestBuilder.execute(handler);
        return future.toCompletableFuture();
    }

    /**
     * LoggingListener
     */
    static class LoggingListener implements WebSocketTextListener {
        private final Consumer<String> onMessageCallback;

        /**
         * Constructor
         * @param onMessageCallback callback
         */
        public LoggingListener(Consumer<String> onMessageCallback) {
            this.onMessageCallback = onMessageCallback;
        }

        private Logger logger = org.slf4j.LoggerFactory.getLogger(LoggingListener.class);

        private Throwable throwableFound = null;

        /**
         * Get throwable
         * @return throwable
         */
        public Throwable getThrowable() {
            return throwableFound;
        }

        /**
         * Action performed on opening socket
         * @param websocket
         */
        public void onOpen(WebSocket websocket) {
        }

        /**
         * Action performed on closing socket
         * @param websocket
         */
        public void onClose(WebSocket websocket) {
        }

        /**
         * Action performed when an error is thrown
         * @param t throwable
         */
        public void onError(Throwable t) {
            throwableFound = t;
        }

        /**
         * Action performed when there is a message
         * @param s String
         */
        @Override
        public void onMessage(String s) {
            onMessageCallback.accept(s);
        }
    }

}
