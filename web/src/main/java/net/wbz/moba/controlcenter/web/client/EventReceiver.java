package net.wbz.moba.controlcenter.web.client;

import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.client.event.RemoteEventService;
import de.novanic.eventservice.client.event.RemoteEventServiceFactory;
import de.novanic.eventservice.client.event.domain.DomainFactory;
import de.novanic.eventservice.client.event.listener.RemoteEventListener;
import net.wbz.moba.controlcenter.web.client.Callbacks.VoidAsyncCallback;

/**
 * Util to register {@link RemoteEventListener} to receiving {@link Event}s from server.
 * Running a single {@link RemoteEventService}.
 *
 * @author Daniel Tuerk
 */
public class EventReceiver {

    private final static EventReceiver instance = new EventReceiver();
    private final RemoteEventService theRemoteEventService;

    private EventReceiver() {
        theRemoteEventService = RemoteEventServiceFactory.getInstance().getRemoteEventService();
    }

    public static EventReceiver getInstance() {
        return instance;
    }

    public void addListener(final Class<? extends Event> eventClazz, RemoteEventListener listener) {
        // TODO try EventFilter
        theRemoteEventService.addListener(DomainFactory.getDomain(eventClazz.getName()), listener);

        // trigger to fire last event from cache
        RequestUtils.getInstance().getBusService().requestResendLastEvent(eventClazz.getName(),
                new VoidAsyncCallback());
    }

    public void removeListener(Class<? extends Event> eventClazz, RemoteEventListener listener) {
        theRemoteEventService.removeListener(DomainFactory.getDomain(eventClazz.getName()), listener);
    }

    /**
     * Manually fire a event from the client to all client listeners.
     * TODO remove? because it will only send to the actual client and where is no chance to use the server event cache
     * 
     * @param event {@link Event}
     */
    public void fireEvent(Event event) {
        theRemoteEventService.addEvent(DomainFactory.getDomain(event.getClass().getName()), event);
    }
}
