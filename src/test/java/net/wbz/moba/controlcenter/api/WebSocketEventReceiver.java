package net.wbz.moba.controlcenter.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.wbz.moba.controlcenter.shared.Event;
import org.awaitility.core.ConditionTimeoutException;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@Slf4j
public class WebSocketEventReceiver {

    private static final String WEBSOCKET_URL = "ws://localhost:8081/websocket";
    private static final int DEFAULT_TIMEOUT_IN_SECONDS = 5;

    private final List<String> receivedMessages = new CopyOnWriteArrayList<>();
    private volatile String clientId;
    private final ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public WebSocketEventReceiver() {
        ensureWebSocketConnected();
    }

    public void reset() {
            log.info("Resetting received messages: {}", receivedMessages.size());
            receivedMessages.clear();
    }

    public <E extends Event> void verifyReceivedEvent(Class<E> eventClazz, String... messageContains) {
        verifyReceivedEvent(DEFAULT_TIMEOUT_IN_SECONDS, eventClazz, messageContains);
    }

    public <E extends Event> E catchEvent(Class<E> eventClazz) {
        return catchEvent(eventClazz, null);
    }

    public <E extends Event> E catchEvent(Class<E> eventClazz, Predicate<String> filter) {
        final AtomicReference<E> eventReference = new AtomicReference<>();
        awaitMessage(DEFAULT_TIMEOUT_IN_SECONDS, () -> List.copyOf(receivedMessages)
            // filter by event
            .stream()
            .filter(s -> s.startsWith(eventClazz.getSimpleName() + ": ")
                && (filter == null || filter.test(s)))
            .findFirst()
            // map to object and remove from list to avoid multiple catches of the same event
            .map(x -> {
                try {
                    eventReference.set(objectMapper.readValue(x.substring(
                        eventClazz.getSimpleName().length() + 2), eventClazz));
                    receivedMessages.remove(x);
                    return true;
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            })
            .orElse(false), "catch message for event: " + eventClazz.getSimpleName());
        return eventReference.get();
    }

    public <E extends Event> void verifyReceivedEvent(int timeoutInSeconds, Class<E> eventClazz, String... messageContains) {
        awaitMessage(timeoutInSeconds, () -> receivedMessages.stream()
                .anyMatch(msg -> msg.startsWith(eventClazz.getSimpleName() + ": ")
                    && Arrays.stream(messageContains).allMatch(msg::contains)),
            "await message for event: " + eventClazz.getSimpleName());
    }

    public String getClientId() {
        awaitMessage(DEFAULT_TIMEOUT_IN_SECONDS, () -> clientId != null && !clientId.isBlank(), "get client id");
        return clientId;
    }

    private static void awaitMessage(int timeoutInSeconds, Callable<Boolean> booleanCallable, String message) {
        try {
        await(message)
            .atMost(timeoutInSeconds, TimeUnit.SECONDS)
            .pollInterval(50, TimeUnit.MILLISECONDS)
            .until(booleanCallable);
        } catch (ConditionTimeoutException e) {
            log.error("""
                    
                    =================================================
                    ============                         ============
                    ============ timeout: test ends here ============
                    ============                         ============
                    =================================================
                    """);
            throw e;
        }
    }

    /**
     * Helper method to connect to WebSocket and initialize it for all tests.
     */
    private void ensureWebSocketConnected() {
        receivedMessages.clear();
        CountDownLatch connectionLatch = new CountDownLatch(1);

        final WebSocketClient webSocketClient = new WebSocketClient(URI.create(WEBSOCKET_URL)) {
            @Override
            public void onOpen(ServerHandshake ignored) {
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
                    log.info("Received WebSocket message: {}", message);
                    receivedMessages.add(message);
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

}
