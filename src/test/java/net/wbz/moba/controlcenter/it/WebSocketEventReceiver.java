package net.wbz.moba.controlcenter.it;

import lombok.extern.slf4j.Slf4j;
import net.wbz.moba.controlcenter.shared.Event;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@Slf4j
class WebSocketEventReceiver {

    private static final String WEBSOCKET_URL = "ws://localhost:8081/websocket";
    private final List<String> receivedMessages = new ArrayList<>();
    private final Object messageLock = new Object();
    private volatile String clientId;

    WebSocketEventReceiver() {
        ensureWebSocketConnected();
    }

    /**
     * Helper method to connect to WebSocket and initialize it for all tests.
     */
    private void ensureWebSocketConnected() {
        receivedMessages.clear();
        CountDownLatch connectionLatch = new CountDownLatch(1);

        final WebSocketClient webSocketClient = new WebSocketClient(URI.create(WEBSOCKET_URL)) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                connectionLatch.countDown();
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
            }

            @Override
            public void onMessage(String message) {
                if (message != null && message.startsWith("clientId: ")) {
                    clientId = message.substring("clientId: ".length()).trim();
                    log.debug("Received WebSocket clientId: {}", clientId);
                } else {
                    synchronized (messageLock) {
                        receivedMessages.add(message);
                    }
                }
            }

            @Override
            public void onError(Exception ex) {
                fail("WebSocket error: " + ex.getMessage());
            }
        };

        webSocketClient.connect();
        try {
            assertTrue(connectionLatch.await(10, TimeUnit.SECONDS), "WebSocket connection timeout");
        } catch (InterruptedException e) {
            throw new RuntimeException("error by starting awaitility", e);
        }
    }

    <E extends Event> void verifyReceivedEvent(Class<E> eventClazz, String... messageContains) {
        await()
            .atMost(5, TimeUnit.SECONDS)
            .pollInterval(50, TimeUnit.MILLISECONDS)
            .until(() -> {
                synchronized (messageLock) {
                    return receivedMessages.stream().peek(msg -> log.info("Received WebSocket message: {}", msg))
                        .anyMatch(msg -> msg.startsWith(eventClazz.getSimpleName() + ": ")
                            && java.util.Arrays.stream(messageContains).allMatch(msg::contains));
                }
            });
    }

    String getClientId() {
        await()
            .atMost(5, TimeUnit.SECONDS)
            .pollInterval(50, TimeUnit.MILLISECONDS)
            .until(() -> clientId != null && !clientId.isBlank());
        return clientId;
    }
}
