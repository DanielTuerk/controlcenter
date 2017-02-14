package net.wbz.moba.controlcenter.web.server.web.editor.block;

import com.google.common.base.Objects;
import net.wbz.moba.controlcenter.web.shared.track.model.Signal;
import net.wbz.moba.controlcenter.web.shared.train.Train;

/**
 * @author Daniel Tuerk
 */
 class SignalBlock {

    private boolean monitoringBlockFree = false;

    private Train waitingTrain;

    private final Signal signal;
    private Train trainInMonitoringBlock;

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
        return Objects.toStringHelper(this)
                .add("monitoringBlockFree", monitoringBlockFree)
                .add("waitingTrain", waitingTrain)
                .add("signal", signal)
                .toString();
    }

    public void setTrainInMonitoringBlock(Train trainInMonitoringBlock) {
        this.trainInMonitoringBlock = trainInMonitoringBlock;
    }

    public Train getTrainInMonitoringBlock() {
        return trainInMonitoringBlock;
    }
}
