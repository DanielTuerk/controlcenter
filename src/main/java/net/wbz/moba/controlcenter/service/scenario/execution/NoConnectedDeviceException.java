package net.wbz.moba.controlcenter.service.scenario.execution;

/**
 * @author Daniel Tuerk
 */
public class NoConnectedDeviceException extends Exception {

    public NoConnectedDeviceException(Exception exception) {
        super("no connected device", exception);
    }

}
