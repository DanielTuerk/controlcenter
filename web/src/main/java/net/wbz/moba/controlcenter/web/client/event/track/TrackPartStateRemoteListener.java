package net.wbz.moba.controlcenter.web.client.event.track;

import net.wbz.moba.controlcenter.web.client.event.RemoteEventListener;
import net.wbz.moba.controlcenter.web.shared.viewer.TrackPartStateEvent;

/**
 * {@link RemoteEventListener} for the {@link TrackPartStateEvent}s.
 *
 * @author Daniel Tuerk
 */
public interface TrackPartStateRemoteListener extends RemoteEventListener<TrackPartStateEvent> {

    @Override
    default void applyEvent(TrackPartStateEvent event) {
        trackPartStateChanged(event);
    }

    @Override
    default Class<TrackPartStateEvent> getRemoteClass() {
        return TrackPartStateEvent.class;
    }

    void trackPartStateChanged(TrackPartStateEvent event);
}
