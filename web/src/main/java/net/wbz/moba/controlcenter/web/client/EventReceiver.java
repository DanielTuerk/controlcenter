package net.wbz.moba.controlcenter.web.client;

import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.client.event.RemoteEventService;
import de.novanic.eventservice.client.event.RemoteEventServiceFactory;
import de.novanic.eventservice.client.event.domain.DomainFactory;
import de.novanic.eventservice.client.event.listener.RemoteEventListener;

/**
 * Util to register {@link RemoteEventListener} to receiving {@link Event}s from server.
 * Running a single {@link RemoteEventService}.
 *
 * @author Daniel Tuerk
 */
public class EventReceiver {

    private final RemoteEventService theRemoteEventService;
    private final static EventReceiver instance = new EventReceiver();

    public static EventReceiver getInstance() {
        return instance;
    }

    private EventReceiver() {
        theRemoteEventService = RemoteEventServiceFactory.getInstance().getRemoteEventService();
    }

    public void addListener(Class<? extends Event> eventClazz, RemoteEventListener listener) {
        theRemoteEventService.addListener(DomainFactory.getDomain(eventClazz.getName()), listener);
    }

    public void removeListener(Class<? extends Event> eventClazz, RemoteEventListener listener) {
        theRemoteEventService.removeListener(DomainFactory.getDomain(eventClazz.getName()), listener);
    }

    /**
     * Manually fire a event from the client to all client listeners.
     *
     * @param event {@link Event}
     */
    public void fireEvent(Event event) {
        theRemoteEventService.addEvent(DomainFactory.getDomain(event.getClass().getName()), event);
    }
}
