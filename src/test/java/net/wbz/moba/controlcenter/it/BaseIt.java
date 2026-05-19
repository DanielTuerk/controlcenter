package net.wbz.moba.controlcenter.it;

import net.wbz.moba.controlcenter.shared.Event;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class BaseIt {
    private static final Logger LOG = LoggerFactory.getLogger(BaseIt.class);

    private static final String WEBSOCKET_URL = "ws://localhost:8081/websocket";
    private static WebSocketClient webSocketClient;
    private static final List<String> receivedMessages = new ArrayList<>();
    private static final Object messageLock = new Object();

    @BeforeAll
    static void setUp() throws Exception {
        ensureWebSocketConnected();
    }

    /**
     * Helper method to connect to WebSocket and initialize it for all tests.
     */
    private static void ensureWebSocketConnected() throws Exception {
        if (webSocketClient != null && webSocketClient.isOpen()) {
            return;
        }

        receivedMessages.clear();
        CountDownLatch connectionLatch = new CountDownLatch(1);

        webSocketClient = new WebSocketClient(URI.create(WEBSOCKET_URL)) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                connectionLatch.countDown();
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
            }

            @Override
            public void onMessage(String message) {
                synchronized (messageLock) {
                    receivedMessages.add(message);
                }
            }

            @Override
            public void onError(Exception ex) {
                fail("WebSocket error: " + ex.getMessage());
            }
        };

        webSocketClient.connect();
        assertTrue(connectionLatch.await(10, TimeUnit.SECONDS), "WebSocket connection timeout");
    }

    protected <E extends Event> void verifyReceivedEvent(Class<E> eventClazz, String messageContains) {
        await()
            .atMost(5, TimeUnit.SECONDS)
            .pollInterval(50, TimeUnit.MILLISECONDS)
            .until(() -> {
                synchronized (messageLock) {
                    return receivedMessages.stream().peek(msg -> LOG.debug("Received WebSocket message: {}", msg))
                        .anyMatch(msg -> msg.startsWith(eventClazz.getSimpleName() + ": ")
                            && msg.contains(messageContains));
                }
            });
    }

}
