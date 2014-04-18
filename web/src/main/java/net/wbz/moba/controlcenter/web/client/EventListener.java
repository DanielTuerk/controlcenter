package net.wbz.moba.controlcenter.web.client;

import de.novanic.eventservice.client.event.Event;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
public interface EventListener<T extends Event> {
    public void apply(T event);
    public Class<? extends Event> getEventClass();
}
