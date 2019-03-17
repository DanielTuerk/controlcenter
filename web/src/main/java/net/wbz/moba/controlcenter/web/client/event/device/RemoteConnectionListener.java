package net.wbz.moba.controlcenter.web.client.event.device;

import net.wbz.moba.controlcenter.web.client.event.RemoteEventListener;
import net.wbz.moba.controlcenter.web.shared.bus.DeviceConnectionEvent;
import net.wbz.moba.controlcenter.web.shared.bus.DeviceInfo;
import net.wbz.moba.controlcenter.web.shared.bus.DeviceInfoEvent;

/**
 * Listener for the connection state of the device.
 *
 * @author Daniel Tuerk
 */
public interface RemoteConnectionListener extends RemoteEventListener<DeviceConnectionEvent> {

    @Override
    default void applyEvent(DeviceConnectionEvent deviceInfoEvent) {
        if (deviceInfoEvent.getEventType() == DeviceConnectionEvent.TYPE.CONNECTED) {
            connected(deviceInfoEvent.getDeviceInfo());
        } else if (deviceInfoEvent.getEventType() == DeviceConnectionEvent.TYPE.DISCONNECTED) {
            disconnected(deviceInfoEvent.getDeviceInfo());
        }
    }

    @Override
    default Class<DeviceConnectionEvent> getRemoteClass() {
        return DeviceConnectionEvent.class;
    }

    void connected(DeviceInfo deviceInfoEvent);

    void disconnected(DeviceInfo deviceInfoEvent);
}
