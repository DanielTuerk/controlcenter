package net.wbz.moba.controlcenter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.websockets.next.OnBinaryMessage;
import io.quarkus.websockets.next.OnClose;
import io.quarkus.websockets.next.OnOpen;
import io.quarkus.websockets.next.WebSocket;
import io.quarkus.websockets.next.WebSocketConnection;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import net.wbz.moba.controlcenter.shared.Event;
import net.wbz.moba.controlcenter.shared.EventCache;
import net.wbz.moba.controlcenter.shared.StateEvent;
import net.wbz.moba.controlcenter.shared.bus.BusDataEvent;
import org.jboss.logging.Logger;

/**
 * TODO migrate (maybe using a lib/quarkus component)
 * <p>
 * Broadcaster for the events
 *
 * @author Daniel Tuerk
 */
@WebSocket(path = "/websocket")
@ApplicationScoped
public class EventBroadcaster {
    private static final Logger LOG = Logger.getLogger(EventBroadcaster.class);

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
        LOG.debugf("Client connected: %s", connection.id());

        // send all missed messages while not connected
        eventCache.getEvents().forEach(cachedEvent ->
            cachedEvent.values()
                .forEach(event -> sendEvent(event, Set.of(connection)))
        );
    }

    @OnClose
    public void onClose(WebSocketConnection connection) {
        connections.remove(connection);
        LOG.debugf("Client disconnected: %s", connection.id());
    }

    @OnBinaryMessage
    public void onBinaryMessage(WebSocketConnection connection, byte[] data) {

    }

    //    private final EventExecutorService eventExecutorService;


//    @Inject
//public EventBroadcaster(DeviceManager deviceManager) {
////        this.eventCache = eventCache;
//    //TODO do nothing now
////        EventExecutorServiceFactory theSF = EventExecutorServiceFactory.getInstance();
////        eventExecutorService = theSF.getEventExecutorService("event");
//
//    deviceManager.addDeviceConnectionListener(new DeviceConnectionListener() {
//        @Override
//        public void connected(Device device) {
//        }
//
//        @Override
//        public void disconnected(Device device) {
//            LOG.info("device disconnected, clear event cache");
////               eventCache.clear();
//        }
//    });
//}

    /***
     * Fire the given event to client.
     *
     * @param event {@link Event}
     */
    public synchronized void fireEvent(Event event) {
//        if (event.getClass() != BusDataEvent.class) {
//            // avoid log spam
//            LOG.debug("fire Event: " + event.toString());
//        }
        sendEvent(event);
        saveLastSendEvent(event);
    }
//
//    /**
//     * Get all events from cache for the given class name.
//     *
//     * @param eventClazzName class name of the event
//     * @return events from cache
//     */
//    public Collection<StateEvent> getLastSendEvents(final String eventClazzName) {
//        return new ArrayList<>(eventCache.getEvents(eventClazzName));
//    }

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
                        LOG.debugf("sending %s to %s".formatted(event, connection.id()));
                    }
                    connection.sendTextAndAwait(
                        "%s: %s".formatted(event.getClass().getSimpleName(), json));
                } catch (Exception e) {
                    LOG.debugf(e, "Failed to send event to %s", connection.id());
                }
            });
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize DTO", e);
        }
    }

    private record Payload(String type, String payload) {
        // TODO use it
    }

    private synchronized void saveLastSendEvent(Event event) {
        if (event instanceof StateEvent) {
            eventCache.addEvent((StateEvent) event);
        }
    }

}