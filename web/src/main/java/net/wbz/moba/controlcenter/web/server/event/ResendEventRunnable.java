package net.wbz.moba.controlcenter.web.server.event;

import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.client.event.domain.DomainFactory;
import de.novanic.eventservice.service.EventExecutorService;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Daniel Tuerk
 */
class ResendEventRunnable implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(ResendEventRunnable.class);

    private static final long SLEEP_IN_MILLIS = 1000L;
    private final String eventClazzName;
    private final Collection<Event> events;
    private final EventExecutorService eventExecutorService;

    ResendEventRunnable(String eventClazzName, Collection<Event> events,
        EventExecutorService eventExecutorService) {
        this.eventClazzName = eventClazzName;
        this.events = events;
        this.eventExecutorService = eventExecutorService;
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

    private void sendEvent(Event event) {
        eventExecutorService.addEvent(DomainFactory.getDomain(event.getClass().getName()), event);
    }

}
