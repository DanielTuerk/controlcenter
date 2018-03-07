package net.wbz.moba.controlcenter.web.client;

import de.novanic.eventservice.client.event.Event;

/**
 * @author Daniel Tuerk
 */
public interface EventListener<T extends Event> {
    void apply(T event);

    Class<? extends Event> getEventClass();
}
