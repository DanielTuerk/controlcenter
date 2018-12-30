package net.wbz.moba.controlcenter.web.server;

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

    private Map<String, Set<Event>> lastSendEvents = Maps.newConcurrentMap();

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
                Sets.newHashSet(lastSendEvents.get(eventClazzName))));
        }
    }

    private void sendEvent(Event event) {
        eventExecutorService.addEvent(DomainFactory.getDomain(event.getClass().getName()), event);
    }

    private synchronized void saveLastSendEvent(Event event) {
        String key = getKey(event);
        if (!lastSendEvents.containsKey(key)) {
            lastSendEvents.put(key, Sets.newConcurrentHashSet());
        }
        lastSendEvents.get(key).remove(event);
        lastSendEvents.get(key).add(event);
    }

    private String getKey(Event eventClazz) {
        return eventClazz.getClass().getName();
    }

    private class ResendEventRunnable implements Runnable {

        private static final long SLEEP_IN_MILLIS = 1000L;
        private final String eventClazzName;
        private final Set<Event> events;

        private ResendEventRunnable(String eventClazzName, Set<Event> events) {
            this.eventClazzName = eventClazzName;
            this.events = events;
        }

        @Override
        public void run() {
            try {
                LOG.debug("wait to resend last events of " + eventClazzName);
                // TODO delay needed or wrong time to call?
                Thread.sleep(SLEEP_IN_MILLIS);
                LOG.debug("request resend last events of " + eventClazzName);

                for (Event event : events) {
                    sendEvent(event);
                }
            } catch (InterruptedException e) {
                LOG.error("error wait to resend", e);
            }
        }
    }
}