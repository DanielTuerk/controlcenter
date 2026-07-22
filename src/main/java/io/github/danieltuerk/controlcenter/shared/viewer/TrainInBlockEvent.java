package io.github.danieltuerk.controlcenter.shared.viewer;

import io.github.danieltuerk.controlcenter.shared.StateEvent;
import io.github.danieltuerk.controlcenter.shared.train.Train;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Schema(description = "train enter or exit block straight event sent via WebSocket")
@Tag(ref = "websocket")
public record TrainInBlockEvent(long trackPartId,
                                boolean enter,
                                int trainAddress,
                                Train.DRIVING_DIRECTION trainDirection) implements StateEvent {

    @Override
    public String getCacheKey() {
        return "%s:%d:%b".formatted(getClass().getName(), trackPartId, enter);
    }

}
