package net.wbz.moba.controlcenter.communication.api;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
public interface InputModule {

    void setInputStream(InputStream inputStream);
    void setOutputStream(OutputStream outputStream);
    public boolean getBitState(Device.BIT bit) throws DeviceAccessException;


}
