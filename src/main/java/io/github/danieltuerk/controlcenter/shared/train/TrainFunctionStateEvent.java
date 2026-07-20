package io.github.danieltuerk.controlcenter.shared.train;

import lombok.Getter;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.Objects;

/**
 * @author Daniel Tuerk
 */
@Getter
@Schema(description = "Train status update sent via WebSocket")
@Tag(ref = "websocket")
public class TrainFunctionStateEvent extends TrainStateEvent {

    private final TrainFunction function;
    private final boolean active;

    public TrainFunctionStateEvent(long itemId, TrainFunction function, boolean active) {
        super(itemId);
        this.function = function;
        this.active = active;
    }

    @Override
    public String getCacheKey() {
        return super.getCacheKey() + "-" + function.getConfiguration().getIdentifierKey();
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        TrainFunctionStateEvent that = (TrainFunctionStateEvent) o;
        return active == that.active && Objects.equals(function, that.function);
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), function, active);
    }

    @Override
    public String toString() {
        return "TrainFunctionStateEvent{" + "function=" + function + ", active=" + active + "} " + super.toString();
    }
}
