package net.wbz.moba.controlcenter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.websockets.next.OnBinaryMessage;
import io.quarkus.websockets.next.OnClose;
import io.quarkus.websockets.next.OnOpen;
import io.quarkus.websockets.next.WebSocket;
import io.quarkus.websockets.next.WebSocketConnection;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.wbz.moba.controlcenter.shared.Event;
import net.wbz.moba.controlcenter.shared.EventCache;
import net.wbz.moba.controlcenter.shared.StateEvent;
import net.wbz.moba.controlcenter.shared.bus.BusDataEvent;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

/**
 * Broadcaster for the events send over websocket.
 *
 * @author Daniel Tuerk
 */
@Slf4j
@WebSocket(path = "/websocket")
@ApplicationScoped
public class EventBroadcaster {

    private final EventCache eventCache;
    private final Set<WebSocketConnection> connections = new CopyOnWriteArraySet<>();
    private final ObjectMapper objectMapper;

    @Inject
    public EventBroadcaster(ObjectMapper objectMapper, EventCache eventCache) {
        this.objectMapper = objectMapper;
        this.eventCache = eventCache;
    }

    @OnOpen
    public void onOpen(WebSocketConnection connection) {
        connections.add(connection);
        log.debug("Client connected: {}", connection.id());

        connection.sendText("clientId: %s".formatted(connection.id()))
            .subscribe().with(
                unused -> {
                },
                failure -> log.error("failed to sent clientId to {}", connection.id(), failure)
            );
        // TODO after server restart, the client need to be triggered to reload, maybe page refresh because the eventchache is empty and by that the server is restarted

        // send all missed messages while not connected
        eventCache.getEvents().forEach(cachedEvent ->
            cachedEvent.values()
                .forEach(event -> sendEvent(event, Set.of(connection)))
        );
    }

    @OnClose
    public void onClose(WebSocketConnection connection) {
        connections.remove(connection);
        log.debug("Client disconnected: {}", connection.id());
    }

    @OnBinaryMessage
    public void onBinaryMessage(WebSocketConnection connection, byte[] data) {
    }

    /***
     * Fire the given event to client.
     *
     * @param event {@link Event}
     */
    public synchronized void fireEvent(Event event) {
        sendEvent(event);
        saveLastSendEvent(event);
    }

    public synchronized void fireTrackingEvent(Event event, Set<String> connectionIds) {
        sendEvent(event, connections.stream()
            .filter(webSocketConnection -> connectionIds.contains(webSocketConnection.id()))
            .collect(Collectors.toSet()));
    }

    private void sendEvent(Event event) {
        sendEvent(event, connections);
    }

    private void sendEvent(Event event, Set<WebSocketConnection> connections) {
        String json;
        try {
            json = objectMapper.writeValueAsString(event);
            connections.forEach(connection -> {
                try {
                    if (event.getClass() != BusDataEvent.class) {
                        // avoid log spam
                        log.debug("sending {} to {}", event, connection.id());
                    }
                    connection.sendText(
                            "%s: %s".formatted(event.getClass().getSimpleName(), json))
                        .subscribe().with(
                            unused -> {
                            },
                            failure -> log.error("failed to sent event to {}", connection.id(), failure)
                        );
                } catch (Exception e) {
                    log.debug("Failed to send event to {}", connection.id(), e);
                }
            });
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize DTO", e);
        }
    }

    private synchronized void saveLastSendEvent(Event event) {
        if (event instanceof StateEvent) {
            eventCache.addEvent((StateEvent) event);
        }
    }

}