package net.wbz.moba.controlcenter.web.client.event;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import de.novanic.eventservice.client.event.Event;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.wbz.moba.controlcenter.web.shared.EventCache;

/**
 * Delegate for the client remote listeners. It's the only listener which is registered to the server and delegate the
 * received events to the {@link RemoteEventListener} instances of the client.
 *
 * @author Daniel Tuerk
 */
class ListenerDelegate implements de.novanic.eventservice.client.event.listener.RemoteEventListener {

    private final List<RemoteEventListener> listeners = new ArrayList<>();
    private final EventCache clientEventCache;

    ListenerDelegate(EventCache clientEventCache) {
        this.clientEventCache = clientEventCache;
    }

    @Override
    public synchronized void apply(Event event) {
        if (event instanceof StateEvent) {
            clientEventCache.addEvent((StateEvent) event);
        }
        synchronized (listeners) {
            listeners.forEach(listener -> Scheduler.get().scheduleDeferred(new EventCommand(listener, event)));
        }
    }

    void addListener(RemoteEventListener<?> listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
        Collection<StateEvent> events = clientEventCache.getEvents(listener.getRemoteClass().getName());
        if (!events.isEmpty()) {
            events.forEach(listener::apply);
        }
    }

    void removeListener(RemoteEventListener<?> listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    boolean isEmpty() {
        return listeners.isEmpty();
    }

    /**
     * Command to call the listener.
     */
    private class EventCommand implements ScheduledCommand {

        private final de.novanic.eventservice.client.event.listener.RemoteEventListener listener;
        private final Event event;

        EventCommand(RemoteEventListener listener, Event event) {
            this.listener = listener;
            this.event = event;
        }

        @Override
        public void execute() {
            listener.apply(event);
        }
    }
}
