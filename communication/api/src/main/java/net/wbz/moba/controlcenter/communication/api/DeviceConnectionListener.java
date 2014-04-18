package net.wbz.moba.controlcenter.communication.api;

import net.wbz.moba.controlcenter.communication.api.Device;

/**
 * Listener for the state of the connection for the {@link net.wbz.moba.controlcenter.communication.api.Device}s.
 *
 * @author daniel.tuerk@w-b-z.com
 */
public interface DeviceConnectionListener {

    public void connected(Device device);
    public void disconnected(Device device);
}
