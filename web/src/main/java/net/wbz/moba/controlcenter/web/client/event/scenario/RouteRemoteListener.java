package net.wbz.moba.controlcenter.web.client.event.scenario;

import net.wbz.moba.controlcenter.web.client.event.RemoteEventListener;
import net.wbz.moba.controlcenter.web.shared.scenario.RoutesChangedEvent;

/**
 * {@link RemoteEventListener} for the {@link RoutesChangedEvent}s.
 *
 * @author Daniel Tuerk
 */
public interface RouteRemoteListener extends RemoteEventListener<RoutesChangedEvent> {

    @Override
    default void applyEvent(RoutesChangedEvent event) {
        routeChanged(event);
    }

    @Override
    default Class<RoutesChangedEvent> getRemoteClass() {
        return RoutesChangedEvent.class;
    }

    void routeChanged(RoutesChangedEvent event);
}
