package io.github.danieltuerk.controlcenter.shared;

import io.github.danieltuerk.controlcenter.shared.device.DeviceConnectionEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cache for {@link StateEvent}s. Each event will be cached by the class name and the {@link StateEvent#getCacheKey()}.
 *
 * @author Daniel Tuerk
 */
@Getter
@Slf4j
@ApplicationScoped
public class EventCache {

    /**
     * Cached events by event class name. Entries contains the events by the cache key.
     */
    private final Map<String, Map<String, StateEvent>> cachedEvents = new ConcurrentHashMap<>();

    public void onDeviceConnectionEvent(@Observes DeviceConnectionEvent event) {
        log.info("device connection changed for device ID: {}", event.deviceInfo().getId());
        if (!event.connected()) {
            log.info("clear event cache for disconnected device (ID: {})", event.deviceInfo().getId());
            cachedEvents.clear();
        }
    }

    /**
     * Add the given event to the cache. Already existing event will be overridden.
     *
     * @apiNote synchronized to guarantee the insert order for later playback
     * @param event {@link E} to cache
     * @param <E> {@link Event} which also implements {@link StateEvent}
     */
    public synchronized <E extends StateEvent> void addEvent(final E event) {
        String key = event.getClass().getName();
        if (!cachedEvents.containsKey(key)) {
            // guarantee the insert order
            cachedEvents.put(key, new LinkedHashMap<>());
        }
        String cacheKey = event.getCacheKey();
        cachedEvents.get(key).put(cacheKey, event);
    }

    public Collection<Map<String, StateEvent>> getEvents() {
        return cachedEvents.values();
    }

    /**
     * Clear the cached events.
     */
    public void clear() {
        cachedEvents.keySet()
            .stream()
            .filter(eventClazzName -> !eventClazzName.equals(DeviceConnectionEvent.class.getName()))
            .forEach(cachedEvents::remove);
    }
}
