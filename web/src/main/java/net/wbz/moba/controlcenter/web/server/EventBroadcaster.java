package net.wbz.moba.controlcenter.web.server;

import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.client.event.domain.DomainFactory;
import de.novanic.eventservice.service.EventExecutorService;
import de.novanic.eventservice.service.EventExecutorServiceFactory;
import net.wbz.moba.controlcenter.web.shared.bus.BusDataEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Daniel Tuerk
 */
@Singleton
public class EventBroadcaster {

    private static final Logger LOG = LoggerFactory.getLogger(EventBroadcaster.class);

    private final EventExecutorService eventExecutorService;

    public EventBroadcaster() {
        EventExecutorServiceFactory theSF = EventExecutorServiceFactory.getInstance();
        eventExecutorService = theSF.getEventExecutorService("event");
    }

    @Transactional
    public synchronized void fireEvent(Event event) {
        if (event.getClass() != BusDataEvent.class) {
            LOG.debug("fire Event: " + event.toString());
        }
        eventExecutorService.addEvent(DomainFactory.getDomain(event.getClass().getName()), event);
    }
}
