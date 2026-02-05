package net.wbz.moba.controlcenter.web.server.web.editor.block;

import net.wbz.moba.controlcenter.web.server.web.train.TrainManager;
import org.jboss.logging.Logger;
import org.slf4j.LoggerFactory;

/**
 * // TODO breaking block
 *
 * @author Daniel Tuerk
 */
public class SignalBreakingBlockListener extends AbstractSignalBlockListener {

    private static final Logger log = Logger.getLogger(SignalBreakingBlockListener.class);

    public SignalBreakingBlockListener(SignalBlock signalBlock, TrainManager trainManager) {
        super(signalBlock.getSignal().getBreakingBlock(), trainManager);
        SignalBlock signalBlock1 = signalBlock;
    }

    @Override
    public void trainEnterBlock(int blockNumber, int trainAddress, boolean forward) {
    }

    @Override
    public void trainLeaveBlock(int blockNumber, int trainAddress, boolean forward) {
    }

    @Override
    public void blockOccupied(int blockNr) {
    }

    @Override
    public void blockFreed(int blockNr) {
    }
}
