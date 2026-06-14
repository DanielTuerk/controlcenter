package net.wbz.moba.controlcenter.shared.viewer;

import net.wbz.moba.controlcenter.shared.StateEvent;
import net.wbz.moba.controlcenter.shared.train.Train;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Schema(description = "track status update sent via WebSocket")
@Tag(ref = "websocket")
public record TrackPartBlockEvent(long trackPartId,
                                  long blockAddress,
                                  int blockNumber,
                                  boolean occupied,
                                  FeedbackBlockData feedbackData) implements StateEvent {

    public record FeedbackBlockData(int trainAddress,
                                    Train.DRIVING_DIRECTION trainDirection) {
    }

    @Override
    public String getCacheKey() {
        return getClass().getName() + ":" + blockAddress + ":" + blockNumber + ":"
            + (feedbackData != null ? feedbackData.trainAddress + "-" + feedbackData.trainDirection.name() : "none");
    }

}
