package net.wbz.moba.controlcenter.web.server.event;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.Singleton;
import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.client.event.domain.DomainFactory;
import de.novanic.eventservice.service.EventExecutorService;
import de.novanic.eventservice.service.EventExecutorServiceFactory;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import net.wbz.moba.controlcenter.web.client.event.StateEvent;
import net.wbz.moba.controlcenter.web.shared.bus.BusDataEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Broadcaster for the events to throw by the {@link EventExecutorService}.
 *
 * @author Daniel Tuerk
 */
@Singleton
public class EventBroadcaster {

    private static final Logger LOG = LoggerFactory.getLogger(EventBroadcaster.class);

    private final EventExecutorService eventExecutorService;
    private final ExecutorService taskExecutor;

    private Map<String, Set<StateEvent>> lastSendEvents = Maps.newConcurrentMap();

    /**
     * Create broadcaster with singleton {@link EventExecutorService}.
     */
    public EventBroadcaster() {
        EventExecutorServiceFactory theSF = EventExecutorServiceFactory.getInstance();
        eventExecutorService = theSF.getEventExecutorService("event");

        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat(getClass().getSimpleName() + "-%d")
            .build();
        // TODO shutdown
        taskExecutor = Executors.newCachedThreadPool(namedThreadFactory);
    }

    /***
     * Fire the given event to client.
     *
     * @param event {@link Event}
     */
    public synchronized void fireEvent(Event event) {
        if (event.getClass() != BusDataEvent.class) {
            // avoid log spam
            LOG.debug("fire Event: " + event.toString());
        }
        sendEvent(event);

        saveLastSendEvent(event);
    }

    /**
     * Send again the last cached event of the given class to the clients.
     *
     * @param eventClazzName name of the event class
     */
    public synchronized void resendEvent(final String eventClazzName) {
        if (lastSendEvents.containsKey(eventClazzName)) {
            taskExecutor.submit(new ResendEventRunnable(eventClazzName,
                Sets.newHashSet(lastSendEvents.get(eventClazzName)), eventExecutorService));
        }
    }

    private void sendEvent(Event event) {
        eventExecutorService.addEvent(DomainFactory.getDomain(event.getClass().getName()), event);
    }

    private synchronized void saveLastSendEvent(Event event) {
        if (event instanceof StateEvent) {
            StateEvent stateEvent = (StateEvent) event;
            String key = getKey(stateEvent);
            if (!lastSendEvents.containsKey(key)) {
                lastSendEvents.put(key, Sets.newConcurrentHashSet());
            }
            lastSendEvents.get(key).remove(stateEvent);
            lastSendEvents.get(key).add(stateEvent);
        }
    }

    private String getKey(Event eventClazz) {
        return eventClazz.getClass().getName();
    }

}