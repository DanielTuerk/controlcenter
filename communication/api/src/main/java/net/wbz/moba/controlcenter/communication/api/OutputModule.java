package net.wbz.moba.controlcenter.communication.api;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
public interface OutputModule {

    public void initialize(OutputStream outputStream, InputStream inputStream);

    public OutputModule setBit(Device.BIT bit, boolean state);

    public boolean getBitState(Device.BIT bit);

    public void sendData();

    public BusDataConsumer getConsumer() ;
}
