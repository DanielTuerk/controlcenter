package net.wbz.moba.controlcenter.web.client.event.track;

import net.wbz.moba.controlcenter.web.client.event.RemoteEventListener;
import net.wbz.moba.controlcenter.web.shared.bus.FeedbackBlockEvent;
import net.wbz.moba.controlcenter.web.shared.viewer.TrackPartBlockEvent;

/**
 * {@link RemoteEventListener} for the {@link TrackPartBlockEvent}s.
 *
 * @author Daniel Tuerk
 */
public interface FeedbackTrackBlockRemoteListener extends RemoteEventListener<FeedbackBlockEvent> {

    @Override
    default void applyEvent(FeedbackBlockEvent event) {
        feedbackTrackBlockStateChanged(event);
    }

    @Override
    default Class<FeedbackBlockEvent> getRemoteClass() {
        return FeedbackBlockEvent.class;
    }

    void feedbackTrackBlockStateChanged(FeedbackBlockEvent event);
}
