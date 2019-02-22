package net.wbz.moba.controlcenter.web.client.event;

import de.novanic.eventservice.client.event.Event;

/**
 * Listener for dedicated {@link Event} of the {@link de.novanic.eventservice.client.event.listener.RemoteEventListener}.
 *
 * @author Daniel Tuerk
 */
public interface RemoteEventListener<T extends Event> extends
    de.novanic.eventservice.client.event.listener.RemoteEventListener {

    default void apply(Event var1) {
        applyEvent((T) var1);
    }

    void applyEvent(T var1);

    Class<T> getRemoteClass();
}
