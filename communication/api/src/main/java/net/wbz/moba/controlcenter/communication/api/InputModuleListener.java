package net.wbz.moba.controlcenter.communication.api;

/**
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
public interface InputModuleListener {

    public void bitStateChanged(Device.BIT bit, boolean newState);
}
