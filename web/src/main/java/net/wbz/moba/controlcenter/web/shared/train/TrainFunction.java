package net.wbz.moba.controlcenter.web.shared.train;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Created by Daniel on 08.03.14.
 */
public class TrainFunction implements IsSerializable {

    public enum FUNCTION {LIGHT, HORN, F1, F2, F3, F4, F5, F6, F7, F8}

    private FUNCTION function;

    private boolean state;

    public TrainFunction() {
    }

    public TrainFunction(FUNCTION function, boolean state) {
        this.function = function;
        this.state = state;
    }

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
