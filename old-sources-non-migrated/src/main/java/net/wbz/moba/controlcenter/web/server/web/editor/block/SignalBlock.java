package net.wbz.moba.controlcenter.web.server.web.editor.block;

import net.wbz.moba.controlcenter.shared.track.model.Signal;
import net.wbz.moba.controlcenter.shared.train.Train;

/**
 * <p>
 * The signal block describe the {@link Signal} which monitors a {@link net.wbz.moba.controlcenter.web.shared.track.model.TrackBlock}.
 * </p>
 * <p>
 * The {@link Signal} itself can have a stop, entering and a breaking block. In each block can be a {@link Train}. Only
 * the stop and montioring block is mandatory. The breaking and entering block is optional and will always have {@code
 * null} as {@link Train} if none of this blocks are configured in the {@link Signal}.
 * </p>
 *
 * @author Daniel Tuerk
 */
class SignalBlock {

    /**
     * {@link Signal} to switch {@link net.wbz.moba.controlcenter.web.shared.track.model.Signal.FUNCTION}.
     */
    private final Signal signal;
    /**
     * Occupied state of the monitoring block.
     */
    private boolean monitoringBlockFree = false;
    /**
     * Actual {@link Train} which is waiting for a free track in the monitoring block.
     */
    private Train waitingTrain;
    /**
     * Actual {@link Train} in the monitoring block.
     */
    private Train trainInMonitoringBlock;
    /**
     * Actual {@link Train} in the stop block.
     */
    private Train trainInStopBlock;
    /**
     * Actual {@link Train} in the entering block.
     */
    private Train trainInEnteringBlock;
    /**
     * Actual {@link Train} in the breaking block.
     */
    private Train trainInBreakingBlock;

    SignalBlock(Signal signal) {
        this.signal = signal;
    }

    public boolean isMonitoringBlockFree() {
        return monitoringBlockFree;
    }

    public void setMonitoringBlockFree(boolean monitoringBlockFree) {
        this.monitoringBlockFree = monitoringBlockFree;
    }

    public Train getWaitingTrain() {
        return waitingTrain;
    }

    public void setWaitingTrain(Train waitingTrain) {
        this.waitingTrain = waitingTrain;
    }

    public Signal getSignal() {
        return signal;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("SignalBlock{");
        sb.append("signal=").append(signal);
        sb.append(", monitoringBlockFree=").append(monitoringBlockFree);
        sb.append(", waitingTrain=").append(waitingTrain);
        sb.append('}');
        return sb.toString();
    }

    public Train getTrainInMonitoringBlock() {
        return trainInMonitoringBlock;
    }

    public void setTrainInMonitoringBlock(Train trainInMonitoringBlock) {
        if ((trainInMonitoringBlock != null && waitingTrain != null) && trainInMonitoringBlock.equals(waitingTrain)) {
            // same train entered the monitoring block, remove as waiting train, if not until happen
            setWaitingTrain(null);
        }
        this.trainInMonitoringBlock = trainInMonitoringBlock;
    }

    public Train getTrainInStopBlock() {
        return trainInStopBlock;
    }

    public void setTrainInStopBlock(Train trainInStopBlock) {
        this.trainInStopBlock = trainInStopBlock;
    }

    public Train getTrainInEnteringBlock() {
        return trainInEnteringBlock;
    }

    public void setTrainInEnteringBlock(Train trainInEnteringBlock) {
        this.trainInEnteringBlock = trainInEnteringBlock;
    }

    public Train getTrainInBreakingBlock() {
        return trainInBreakingBlock;
    }

    public void setTrainInBreakingBlock(Train trainInBreakingBlock) {
        this.trainInBreakingBlock = trainInBreakingBlock;
    }

    public boolean isTrainInAnyBlock() {
        return trainInStopBlock != null || trainInMonitoringBlock != null || trainInEnteringBlock != null
            || trainInBreakingBlock != null;
    }
}
