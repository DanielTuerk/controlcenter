package net.wbz.moba.controlcenter.web.client.model.track.block;

import net.wbz.moba.controlcenter.web.shared.track.model.BusDataConfiguration;
import net.wbz.moba.controlcenter.web.shared.train.Train;

/**
 * Mark widget that can receive the feedback state for a {@link Train}.
 *
 * @author Daniel Tuerk
 */
public interface Feedbackable {

    /**
     * Check that block from given {@link BusDataConfiguration} is present.
     *
     * @param configAsIdentifier {@link BusDataConfiguration} for block
     * @return {@code true} for exiting block of configuration
     */
    boolean hasBlock(BusDataConfiguration configAsIdentifier);

    /**
     * Show the information of the given {@link net.wbz.moba.controlcenter.web.shared.train.Train} on this block
     * element.
     *
     * @param train {@link net.wbz.moba.controlcenter.web.shared.train.Train}
     */
    void showTrainOnBlock(Train train);

    /**
     * Remove the element for the given {@link net.wbz.moba.controlcenter.web.shared.train.Train} from this block.
     *
     * @param train {@link net.wbz.moba.controlcenter.web.shared.train.Train}
     */
    void removeTrainOnBlock(Train train);

}
