package net.wbz.moba.controlcenter.web.shared;

import com.google.common.collect.Maps;
import de.novanic.eventservice.client.event.Event;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import net.wbz.moba.controlcenter.web.client.event.StateEvent;
import net.wbz.moba.controlcenter.web.shared.bus.DeviceConnectionEvent;

/**
 * Cache for {@link StateEvent}s. Each event will be cached by the class name and the {@link StateEvent#getCacheKey()}.
 *
 * @author Daniel Tuerk
 */
public class EventCache {

    /**
     * Cached events by event class name. Entries contains the events by the cache key.
     */
    private final Map<String, Map<String, Event>> cachedEvents = Maps.newConcurrentMap();

    /**
     * Add the given event to the cache. Already existing event will be overridden.
     *
     * @param event {@link E} to cache
     * @param <E> {@link Event} which also implements {@link StateEvent}
     */
    public <E extends Event & StateEvent> void addEvent(final E event) {
        String key = event.getClass().getName();
        if (!cachedEvents.containsKey(key)) {
            cachedEvents.put(key, Maps.newConcurrentMap());
        }
        String cacheKey = event.getCacheKey();
        cachedEvents.get(key).put(cacheKey, event);
    }

    /**
     * Receive all events from cache for the given event class name.
     *
     * @param eventClazzName name of {@link Event} class
     * @return cached {@link Event}s
     */
    public Collection<Event> getEvents(final String eventClazzName) {
        if (cachedEvents.containsKey(eventClazzName)) {
            return cachedEvents.get(eventClazzName).values();
        }
        return new ArrayList<>();
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
