package net.wbz.moba.controlcenter.web.shared.train;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
public class TrainFunctionStateEvent extends TrainStateEvent {
    private byte functionAddress;
    private int functionBit;
    private boolean active;

    public TrainFunctionStateEvent(byte functionAddress, int functionBit, boolean active) {
        this.functionAddress = functionAddress;
        this.functionBit = functionBit;
        this.active = active;
    }

    public TrainFunctionStateEvent() {
    }

    public byte getFunctionAddress() {
        return functionAddress;
    }

    public int getFunctionBit() {
        return functionBit;
    }

    public boolean isActive() {
        return active;
    }
}
