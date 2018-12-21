package net.wbz.moba.controlcenter.web.client.event.bus;

import net.wbz.moba.controlcenter.web.client.event.RemoteEventListener;
import net.wbz.moba.controlcenter.web.shared.bus.RecordingEvent;

/**
 * {@link RemoteEventListener} for the {@link RecordingEvent}s.
 *
 * @author Daniel Tuerk
 */
public interface BusRecordingRemoteListener extends RemoteEventListener<RecordingEvent> {

    @Override
    default void applyEvent(RecordingEvent event) {
        switch (event.getState()) {
            case START:
                start(event);
                break;
            case STOP:
                stop(event);
                break;
        }
    }

    @Override
    default Class<RecordingEvent> getRemoteClass() {
        return RecordingEvent.class;
    }

    void start(RecordingEvent event);

    void stop(RecordingEvent event);
}
