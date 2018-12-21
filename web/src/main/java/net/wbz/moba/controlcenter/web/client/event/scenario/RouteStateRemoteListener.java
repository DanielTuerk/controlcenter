package net.wbz.moba.controlcenter.web.client.event.scenario;

import net.wbz.moba.controlcenter.web.client.event.RemoteEventListener;
import net.wbz.moba.controlcenter.web.shared.scenario.RouteStateEvent;

/**
 * {@link RemoteEventListener} for the {@link RouteStateEvent}s.
 *
 * @author Daniel Tuerk
 */
public interface RouteStateRemoteListener extends RemoteEventListener<RouteStateEvent> {

    @Override
    default void applyEvent(RouteStateEvent event) {
        routeStateChanged(event);
    }

    @Override
    default Class<RouteStateEvent> getRemoteClass() {
        return RouteStateEvent.class;
    }

    void routeStateChanged(RouteStateEvent event);
}
