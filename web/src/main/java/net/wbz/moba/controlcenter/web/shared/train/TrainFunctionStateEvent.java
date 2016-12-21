package net.wbz.moba.controlcenter.web.shared.train;

import net.wbz.moba.controlcenter.web.server.persist.train.TrainFunctionEntity;

/**
 * @author Daniel Tuerk
 */
public class TrainFunctionStateEvent extends TrainStateEvent {
    private TrainFunctionEntity.FUNCTION function;
    private boolean active;

    public TrainFunctionStateEvent(long itemId, TrainFunctionEntity.FUNCTION function, boolean active) {
        super(itemId);
        this.function = function;
        this.active = active;
    }

    public TrainFunctionStateEvent() {
    }

    public TrainFunctionEntity.FUNCTION getFunction() {
        return function;
    }

    public boolean isActive() {
        return active;
    }
}
