package net.wbz.moba.controlcenter.web.server.web.editor.block;

import com.google.common.base.Objects;
import net.wbz.moba.controlcenter.web.server.web.train.TrainManager;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackBlock;
import net.wbz.moba.controlcenter.web.shared.train.Train;
import net.wbz.selectrix4java.block.FeedbackBlockListener;

/**
 * Abstract {@link FeedbackBlockListener} for a {@link TrackBlock}.
 *
 * @author Daniel Tuerk
 */
abstract class AbstractSignalBlockListener implements FeedbackBlockListener {

    private final TrackBlock trackBlock;
    private final TrainManager trainManager;

    AbstractSignalBlockListener(TrackBlock trackBlock, TrainManager trainManager) {
        this.trackBlock = trackBlock;
        this.trainManager = trainManager;
    }

    TrackBlock getTrackBlock() {
        return trackBlock;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("trackBlock", trackBlock)
                .toString();
    }

    protected Train getTrain(int trainAddress) {
        return trainManager.getTrain(trainAddress);
    }
}
