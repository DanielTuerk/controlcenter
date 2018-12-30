package net.wbz.moba.controlcenter.web.client.event;

import com.google.common.collect.Maps;
import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.client.event.RemoteEventService;
import de.novanic.eventservice.client.event.RemoteEventServiceFactory;
import de.novanic.eventservice.client.event.domain.DomainFactory;
import de.novanic.eventservice.client.event.listener.RemoteEventListener;
import java.util.Map;
import net.wbz.moba.controlcenter.web.client.request.Callbacks.VoidAsyncCallback;
import net.wbz.moba.controlcenter.web.client.request.RequestUtils;
import net.wbz.moba.controlcenter.web.client.util.Log;

/**
 * Util to register {@link RemoteEventListener} to receiving {@link Event}s from server.
 * Running a single {@link RemoteEventService}.
 *
 * @author Daniel Tuerk
 */
public class EventReceiver {

    private final static EventReceiver instance = new EventReceiver();
    private final RemoteEventService theRemoteEventService;
    private final Map<Class<? extends Event>, ListenerDelegate> listenersByEvent = Maps.newConcurrentMap();

    private EventReceiver() {
        theRemoteEventService = RemoteEventServiceFactory.getInstance().getRemoteEventService();
    }

    public static EventReceiver getInstance() {
        return instance;
    }

    public void addListener(net.wbz.moba.controlcenter.web.client.event.RemoteEventListener... listeners) {
        for (net.wbz.moba.controlcenter.web.client.event.RemoteEventListener listener : listeners) {
            addListener(listener.getRemoteClass(), listener);
        }
    }

    public void removeListener(net.wbz.moba.controlcenter.web.client.event.RemoteEventListener... listeners) {
        for (net.wbz.moba.controlcenter.web.client.event.RemoteEventListener listener : listeners) {
            removeListener(listener.getRemoteClass(), listener);
        }
    }

    public void addListener(final Class<? extends Event> eventClazz, RemoteEventListener listener) {
        if (!listenersByEvent.containsKey(eventClazz)) {

            final ListenerDelegate delegate = new ListenerDelegate();
            delegate.addListener(listener);
            listenersByEvent.put(eventClazz, delegate);

            // register only the delegate
            theRemoteEventService.addListener(DomainFactory.getDomain(eventClazz.getName()), delegate);

            // trigger to fire last event from cache
            RequestUtils.getInstance().getBusService().requestResendLastEvent(eventClazz.getName(),
                new VoidAsyncCallback());

        } else {
            listenersByEvent.get(eventClazz).addListener(listener);
        }
    }

    public void removeListener(Class<? extends Event> eventClazz, RemoteEventListener listener) {
        if (listenersByEvent.containsKey(eventClazz)) {
            ListenerDelegate delegate = listenersByEvent.get(eventClazz);
            delegate.removeListener(listener);

            if (delegate.isEmpty()) {
                // remove the delegate then no more listeners are registered
                theRemoteEventService.removeListener(DomainFactory.getDomain(eventClazz.getName()), delegate);
                listenersByEvent.remove(eventClazz);
            }
        } else {
            Log.error("no listener registered for " + eventClazz.getSimpleName());
        }
    }

}
