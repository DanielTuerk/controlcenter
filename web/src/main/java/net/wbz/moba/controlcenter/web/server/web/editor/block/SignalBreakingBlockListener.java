package net.wbz.moba.controlcenter.web.server.web.editor.block;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.wbz.selectrix4java.block.FeedbackBlockListener;

/**
 * // TODO breaking block
 * 
 * @author Daniel Tuerk
 */
public class SignalBreakingBlockListener extends AbstractSignalBlockListener {
    private static final Logger log = LoggerFactory.getLogger(SignalBreakingBlockListener.class);

    private final SignalBlock signalBlock;

    public SignalBreakingBlockListener(SignalBlock signalBlock) {
        super(signalBlock.getSignal().getBreakingBlock());
        this.signalBlock = signalBlock;
    }

    @Override
    public void trainEnterBlock(int blockNumber, int trainAddress, boolean forward) {
        // TODO break
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
