package net.wbz.moba.controlcenter.web.client.event;

import com.google.common.collect.Maps;
import com.google.gwt.core.client.GWT;
import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.client.event.RemoteEventService;
import de.novanic.eventservice.client.event.RemoteEventServiceFactory;
import de.novanic.eventservice.client.event.domain.DomainFactory;
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

    private final static EventReceiver INSTANCE = GWT.create(EventReceiver.class);
    private final RemoteEventService theRemoteEventService;
    private final Map<Class<? extends Event>, ListenerDelegate> listenersByEvent = Maps.newConcurrentMap();

    private EventReceiver() {
        theRemoteEventService = RemoteEventServiceFactory.getInstance().getRemoteEventService();
    }

    public static synchronized EventReceiver getInstance() {
        return INSTANCE;
    }

    public boolean isActive() {
        return theRemoteEventService.isActive();
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

    private static <T> boolean isInstanceOf(Class<T> type, Object object) {
        try {
            T objectAsType = (T) object;
        } catch (ClassCastException exception) {
            return false;
        }
        return true;
    }

    public void addListener(final Class<? extends Event> eventClazz,
        net.wbz.moba.controlcenter.web.client.event.RemoteEventListener listener) {
        if (!listenersByEvent.containsKey(eventClazz)) {
            Log.debug("add new listener delegate for listener: " + listener.getClass().getName());
            final ListenerDelegate delegate = new ListenerDelegate();
            delegate.addListener(listener);
            listenersByEvent.put(eventClazz, delegate);

            // register only the delegate
            theRemoteEventService.addListener(DomainFactory.getDomain(eventClazz.getName()), delegate);

            if (isInstanceOf(StateEvent.class, eventClazz)) {
                // trigger to fire last event from server cache
                RequestUtils.getInstance().getBusService().requestResendLastEvent(eventClazz.getName(),
                    new VoidAsyncCallback());
            }

        } else {
            listenersByEvent.get(eventClazz).addListener(listener);
        }
    }

    public void removeListener(Class<? extends Event> eventClazz,
        net.wbz.moba.controlcenter.web.client.event.RemoteEventListener listener) {
        if (listenersByEvent.containsKey(eventClazz)) {

            ListenerDelegate delegate = listenersByEvent.get(eventClazz);
            delegate.removeListener(listener);

            if (delegate.isEmpty()) {
                Log.debug("remove listener delegate for listener: " + listener.getClass().getName());
                // remove the delegate then no more listeners are registered
                theRemoteEventService.removeListener(DomainFactory.getDomain(eventClazz.getName()), delegate);
                listenersByEvent.remove(eventClazz);
            }
        } else {
            Log.error("no listener registered for " + eventClazz.getSimpleName());
        }
    }

}
