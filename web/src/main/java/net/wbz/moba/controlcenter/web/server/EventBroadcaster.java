package net.wbz.moba.controlcenter.web.server;

import com.google.inject.Singleton;
import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.client.event.domain.DomainFactory;
import de.novanic.eventservice.service.EventExecutorService;
import de.novanic.eventservice.service.EventExecutorServiceFactory;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
@Singleton
public class EventBroadcaster {
    private final EventExecutorService eventExecutorService;

    public EventBroadcaster() {
        EventExecutorServiceFactory theSF = EventExecutorServiceFactory.getInstance();
        eventExecutorService = theSF.getEventExecutorService("foobar");
    }

    public synchronized void fireEvent(Event event) {
        eventExecutorService.addEvent(DomainFactory.getDomain(event.getClass().getName()), event);
    }
}
