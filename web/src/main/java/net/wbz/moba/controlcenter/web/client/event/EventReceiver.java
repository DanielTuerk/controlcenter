package net.wbz.moba.controlcenter.web.client.event;

import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.client.event.RemoteEventService;
import de.novanic.eventservice.client.event.RemoteEventServiceFactory;
import de.novanic.eventservice.client.event.domain.DomainFactory;
import de.novanic.eventservice.client.event.listener.RemoteEventListener;
import net.wbz.moba.controlcenter.web.client.request.Callbacks.VoidAsyncCallback;
import net.wbz.moba.controlcenter.web.client.request.RequestUtils;

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

    public void addListener(net.wbz.moba.controlcenter.web.client.event.RemoteEventListener listener) {
        addListener(listener.getRemoteClass(), listener);
    }
    public void removeListener(net.wbz.moba.controlcenter.web.client.event.RemoteEventListener listener) {
        removeListener(listener.getRemoteClass(), listener);
    }

    public void addListener(final Class<? extends Event> eventClazz, RemoteEventListener listener) {
        theRemoteEventService.addListener(DomainFactory.getDomain(eventClazz.getName()), listener);

        // trigger to fire last event from cache
        RequestUtils.getInstance().getBusService().requestResendLastEvent(eventClazz.getName(),
                new VoidAsyncCallback());
    }

    public void removeListener(Class<? extends Event> eventClazz, RemoteEventListener listener) {
        theRemoteEventService.removeListener(DomainFactory.getDomain(eventClazz.getName()), listener);
    }

}
