package net.wbz.moba.controlcenter.web.client.event.track;

import net.wbz.moba.controlcenter.web.client.event.RemoteEventListener;
import net.wbz.moba.controlcenter.web.shared.viewer.TrackPartBlockEvent;

/**
 * {@link RemoteEventListener} for the {@link TrackPartBlockEvent}s.
 *
 * @author Daniel Tuerk
 */
public interface TrackBlockRemoteListener extends RemoteEventListener<TrackPartBlockEvent> {

    @Override
    default void applyEvent(TrackPartBlockEvent event) {
        trackBlockStateChanged(event);
    }

    @Override
    default Class<TrackPartBlockEvent> getRemoteClass() {
        return TrackPartBlockEvent.class;
    }

    void trackBlockStateChanged(TrackPartBlockEvent event);
}
