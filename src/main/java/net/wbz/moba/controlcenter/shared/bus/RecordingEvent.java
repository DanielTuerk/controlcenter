package net.wbz.moba.controlcenter.shared.bus;

import net.wbz.moba.controlcenter.shared.StateEvent;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * @author Daniel Tuerk
 */
@Schema(description = "bus status update sent via WebSocket")
@Tag(ref = "websocket")
public class RecordingEvent implements StateEvent {

    private STATE state;
    private String filePath;
    public RecordingEvent() {
    }

    public RecordingEvent(STATE state) {
        this.state = state;
    }

    public RecordingEvent(STATE state, String filePath) {
        this.state = state;
        this.filePath = filePath;
    }

    public STATE getState() {
        return state;
    }

    public String getFilePath() {
        return filePath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RecordingEvent that = (RecordingEvent) o;
        return super.equals(o) && java.util.Objects.equals(getFilePath(), that.getFilePath());
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(super.hashCode(), getFilePath());
    }

    public enum STATE {
        START, STOP
    }
}
