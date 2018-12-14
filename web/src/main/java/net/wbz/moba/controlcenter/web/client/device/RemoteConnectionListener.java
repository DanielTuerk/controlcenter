package net.wbz.moba.controlcenter.web.client.device;

import net.wbz.moba.controlcenter.web.client.event.RemoteEventListener;
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
            connected();
        } else if (deviceInfoEvent.getEventType() == DeviceInfoEvent.TYPE.DISCONNECTED) {
            disconnected();
        }
    }

    @Override
    default Class<DeviceInfoEvent> getRemoteClass() {
        return DeviceInfoEvent.class;
    }

    void connected();

    void disconnected();
}
