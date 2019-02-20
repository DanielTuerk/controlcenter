package net.wbz.moba.controlcenter.web.client.event;

import de.novanic.eventservice.client.event.Event;

/**
 * {@link Event} which represents a state change. All kind of this event will be cached on the server side and can be
 * triggered to resend to the client.
 *
 * @author Daniel Tuerk
 */
public interface StateEvent extends Event {

}
