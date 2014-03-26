package net.wbz.moba.controlcenter.communication.api;

/**
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
public interface BusDataReceiver {

    public void recevied(int busNr, byte[] data);
}
