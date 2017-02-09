package net.wbz.moba.controlcenter.web.server.web.editor.block;

import com.google.common.base.Objects;
import net.wbz.moba.controlcenter.web.shared.track.model.BusDataConfiguration;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackBlock;
import net.wbz.selectrix4java.block.FeedbackBlockListener;

/**
 * @author Daniel Tuerk
 */
abstract class AbstractSignalBlockListener implements FeedbackBlockListener {

    private final TrackBlock trackBlock;

    AbstractSignalBlockListener(TrackBlock trackBlock) {
        this.trackBlock = trackBlock;
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
}
