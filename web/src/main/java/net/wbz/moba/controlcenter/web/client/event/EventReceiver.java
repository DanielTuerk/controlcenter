package net.wbz.moba.controlcenter.web.client.event;

import com.google.common.collect.Maps;
import com.google.gwt.core.client.GWT;
import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.client.event.RemoteEventService;
import de.novanic.eventservice.client.event.RemoteEventServiceFactory;
import de.novanic.eventservice.client.event.domain.DomainFactory;
import java.util.Collection;
import java.util.Map;
import net.wbz.moba.controlcenter.web.client.request.Callbacks.OnlySuccessAsyncCallback;
import net.wbz.moba.controlcenter.web.client.request.RequestUtils;
import net.wbz.moba.controlcenter.web.client.util.Log;
import net.wbz.moba.controlcenter.web.shared.EventCache;

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

    /**
     * Client side {@link EventCache} to cache all received events from server side and delegate to new added
     * listeners.
     */
    private final EventCache clientEventCache = GWT.create(EventCache.class);

    private EventReceiver() {
        theRemoteEventService = RemoteEventServiceFactory.getInstance().getRemoteEventService();
    }

    public static EventReceiver getInstance() {
        return INSTANCE;
    }

    public boolean isActive() {
        return theRemoteEventService.isActive();
    }

    @SafeVarargs
    public final void addListener(
        net.wbz.moba.controlcenter.web.client.event.RemoteEventListener<? extends Event>... listeners) {
        for (net.wbz.moba.controlcenter.web.client.event.RemoteEventListener<? extends Event> listener : listeners) {
            addListener(listener.getRemoteClass(), listener);
        }
    }

    @SafeVarargs
    public final void removeListener(
        net.wbz.moba.controlcenter.web.client.event.RemoteEventListener<? extends Event>... listeners) {
        for (net.wbz.moba.controlcenter.web.client.event.RemoteEventListener<? extends Event> listener : listeners) {
            removeListener(listener.getRemoteClass(), listener);
        }
    }

    public synchronized void addListener(final Class<? extends Event> eventClazz,
        net.wbz.moba.controlcenter.web.client.event.RemoteEventListener listener) {
        if (!listenersByEvent.containsKey(eventClazz)) {
            final ListenerDelegate delegate = new ListenerDelegate(clientEventCache);
            delegate.addListener(listener);
            listenersByEvent.put(eventClazz, delegate);

            // register only the delegate
            theRemoteEventService.addListener(DomainFactory.getDomain(eventClazz.getName()), delegate);

            if (isInstanceOf(StateEvent.class, eventClazz)) {
                // trigger to fire last event from server cache
                RequestUtils.getInstance().getBusService().getLastSendEvent(eventClazz.getName(),
                    new OnlySuccessAsyncCallback<Collection<StateEvent>>() {
                        @Override
                        public void onSuccess(Collection<StateEvent> result) {
                            result.forEach(delegate::apply);
                        }
                    });
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
                // remove the delegate then no more listeners are registered
                theRemoteEventService.removeListener(DomainFactory.getDomain(eventClazz.getName()), delegate);
                listenersByEvent.remove(eventClazz);
            }
        } else {
            Log.error("no listener registered for " + eventClazz.getSimpleName());
        }
    }

    /**
     * I'm the ugly workaround for {@code Class.isAssignable()}.
     */
    private static <T> boolean isInstanceOf(Class<T> type, Object object) {
        try {
            T objectAsType = (T) object;
        } catch (ClassCastException exception) {
            return false;
        }
        return true;
    }

}
