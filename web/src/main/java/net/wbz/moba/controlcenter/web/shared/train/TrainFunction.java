package net.wbz.moba.controlcenter.web.shared.train;

import com.google.gwt.user.client.rpc.IsSerializable;
import net.wbz.moba.controlcenter.web.shared.track.model.AbstractDto;

import java.io.Serializable;

/**
 * @author Daniel Tuerk
 */
public class TrainFunction extends AbstractDto {
    /**
     * Available functions for the train.
     */
    public enum FUNCTION implements Serializable, IsSerializable {LIGHT, HORN, F1, F2, F3, F4, F5, F6, F7, F8}

    private TrainFunction.FUNCTION function;

    private boolean state;

    public FUNCTION getFunction() {
        return function;
    }

    public void setFunction(FUNCTION function) {
        this.function = function;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }
}
