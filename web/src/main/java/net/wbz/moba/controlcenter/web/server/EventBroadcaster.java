package net.wbz.moba.controlcenter.web.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;

import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.client.event.domain.DomainFactory;
import de.novanic.eventservice.service.EventExecutorService;
import de.novanic.eventservice.service.EventExecutorServiceFactory;
import net.wbz.moba.controlcenter.web.shared.bus.BusDataEvent;

/**
 * Broadcaster for the events to throw by the {@link EventExecutorService}.
 *
 * @author Daniel Tuerk
 */
@Singleton
public class EventBroadcaster {

    private static final Logger LOG = LoggerFactory.getLogger(EventBroadcaster.class);

    private final EventExecutorService eventExecutorService;

    /**
     * Create broadcaster with singleton {@link EventExecutorService}.
     */
    public EventBroadcaster() {
        EventExecutorServiceFactory theSF = EventExecutorServiceFactory.getInstance();
        eventExecutorService = theSF.getEventExecutorService("event");
    }

    /***
     * Fire the given event to client.
     *
     * @param event {@link Event}
     */
    public synchronized void fireEvent(Event event) {
        if (event.getClass() != BusDataEvent.class) {
            LOG.debug("fire Event: " + event.toString());
        }
        eventExecutorService.addEvent(DomainFactory.getDomain(event.getClass().getName()), event);
    }
}
