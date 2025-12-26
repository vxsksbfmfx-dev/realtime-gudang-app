package api;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URI;
import java.util.function.Consumer;

public class WebSocketClientHandler extends WebSocketClient {
    private final Consumer<String> messageHandler;

    public WebSocketClientHandler(URI serverUri, Consumer<String> messageHandler) {
        super(serverUri);
        this.messageHandler = messageHandler;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("WebSocket connected");
    }

    @Override
    public void onMessage(String message) {
        messageHandler.accept(message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("WebSocket closed: " + reason);
        new Thread(() -> {
            try {
                Thread.sleep(5000);
                reconnect();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("WebSocket error: " + ex.getMessage());
    }
}