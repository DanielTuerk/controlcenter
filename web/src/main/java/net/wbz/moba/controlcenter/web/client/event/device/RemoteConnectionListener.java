package net.wbz.moba.controlcenter.web.client.event.device;

import net.wbz.moba.controlcenter.web.client.event.RemoteEventListener;
import net.wbz.moba.controlcenter.web.shared.bus.DeviceInfo;
import net.wbz.moba.controlcenter.web.shared.bus.DeviceInfoEvent;

/**
 * Listener for the connection state of the device.
 *
 * @author Daniel Tuerk
 */
public interface RemoteConnectionListener extends RemoteEventListener<DeviceInfoEvent> {

    @Override
    default void applyEvent(DeviceInfoEvent deviceInfoEvent) {
        if (deviceInfoEvent.getEventType() == DeviceInfoEvent.TYPE.CONNECTED) {
            connected(deviceInfoEvent.getDeviceInfo());
        } else if (deviceInfoEvent.getEventType() == DeviceInfoEvent.TYPE.DISCONNECTED) {
            disconnected(deviceInfoEvent.getDeviceInfo());
        }
    }

    @Override
    default Class<DeviceInfoEvent> getRemoteClass() {
        return DeviceInfoEvent.class;
    }

    void connected(DeviceInfo deviceInfoEvent);

    void disconnected(DeviceInfo deviceInfoEvent);
}
