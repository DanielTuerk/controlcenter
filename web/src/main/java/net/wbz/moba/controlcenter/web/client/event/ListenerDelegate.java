package net.wbz.moba.controlcenter.web.client.event;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.client.event.listener.RemoteEventListener;
import java.util.ArrayList;
import java.util.List;
import net.wbz.moba.controlcenter.web.client.util.Log;

/**
 * Delegate for the client remote listeners. It's the only listener which is registered to the server and delegate the
 * received events to the {@link RemoteEventListener} instances of the client.
 *
 * @author Daniel Tuerk
 */
class ListenerDelegate implements de.novanic.eventservice.client.event.listener.RemoteEventListener {

    private final List<de.novanic.eventservice.client.event.listener.RemoteEventListener> listeners = new ArrayList<>();
    private Event lastReceivedEvent;

    @Override
    public synchronized void apply(Event event) {
        if (event instanceof StateEvent) {
            lastReceivedEvent = event;
        }
        synchronized (listeners) {
            listeners.forEach(listener -> Scheduler.get().scheduleDeferred(new EventCommand(listener, event)));
        }
    }

    void addListener(de.novanic.eventservice.client.event.listener.RemoteEventListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
        if (lastReceivedEvent != null) {
            Log.info("resend last received event: " + lastReceivedEvent);
            listener.apply(lastReceivedEvent);
        }
    }

    void removeListener(de.novanic.eventservice.client.event.listener.RemoteEventListener listener) {
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
