package net.wbz.moba.controlcenter.web.server.web.editor;

import net.wbz.moba.controlcenter.web.shared.track.model.Signal;
import net.wbz.moba.controlcenter.web.shared.train.Train;

/**
 * @author Daniel Tuerk
 */
 class SignalBlock {

    private boolean monitoringBlockFree = false;

    private Train waitingTrain;

    private final Signal signal;

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
}
