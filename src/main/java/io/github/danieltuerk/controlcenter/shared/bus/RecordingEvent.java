package io.github.danieltuerk.controlcenter.shared.bus;

import io.github.danieltuerk.controlcenter.shared.StateEvent;
import lombok.Getter;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * @author Daniel Tuerk
 */
@Getter
@Schema(description = "bus status update sent via WebSocket")
@Tag(ref = "websocket")
public class RecordingEvent implements StateEvent {

    private final RECORDING_STATE state;
    private String filePath;

    public RecordingEvent(RECORDING_STATE state) {
        this.state = state;
    }

    public RecordingEvent(RECORDING_STATE state, String filePath) {
        this.state = state;
        this.filePath = filePath;
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

    public enum RECORDING_STATE {
        START, STOP
    }
}
