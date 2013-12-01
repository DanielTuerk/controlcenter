package net.wbz.moba.controlcenter.communication.api;

/**
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
public class DeviceAccessException extends Exception {
    public DeviceAccessException(String s) {
        super(s);
    }

    public DeviceAccessException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
