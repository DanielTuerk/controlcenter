package net.wbz.moba.controlcenter.communication.api;

import java.io.IOException;
import java.io.Serializable;

/**
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
public interface Device extends Serializable {

    public boolean getRailVoltage() throws IOException;

    public void setRailVoltage(boolean state) throws IOException;

    public enum BIT {BIT_1, BIT_2, BIT_3, BIT_4, BIT_5, BIT_6, BIT_7, BIT_8}

    public void connect();
    public void disconnect();
    public boolean isConnected();

    public OutputModule getOutputModule(byte address) throws DeviceAccessException;

    public InputModule getInputModule(byte address) throws DeviceAccessException;

    public OutputModule getTrainModule(byte address) throws DeviceAccessException;

}
