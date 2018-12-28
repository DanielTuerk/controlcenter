package net.wbz.moba.controlcenter.web.client.event.device;

import net.wbz.moba.controlcenter.web.client.event.RemoteEventListener;
import net.wbz.moba.controlcenter.web.shared.bus.DeviceInfo;
import net.wbz.moba.controlcenter.web.shared.bus.DeviceInfoEvent;
import net.wbz.moba.controlcenter.web.shared.bus.DeviceInfoEvent.TYPE;

/**
 * Listener for the the devices.
 *
 * @author Daniel Tuerk
 */
public interface RemoteDeviceListener extends RemoteEventListener<DeviceInfoEvent> {

    @Override
    default void applyEvent(DeviceInfoEvent deviceInfoEvent) {
        if (deviceInfoEvent.getEventType() == TYPE.CREATE) {
            deviceCreated(deviceInfoEvent.getDeviceInfo());
        } else if (deviceInfoEvent.getEventType() == TYPE.REMOVE) {
            deviceRemoved(deviceInfoEvent.getDeviceInfo());
        } else if (deviceInfoEvent.getEventType() == TYPE.MODIFY) {
            devicesModified(deviceInfoEvent.getDeviceInfo());
        }
    }

    @Override
    default Class<DeviceInfoEvent> getRemoteClass() {
        return DeviceInfoEvent.class;
    }

    void devicesModified(DeviceInfo deviceInfo);

    void deviceCreated(DeviceInfo deviceInfoEvent);

    void deviceRemoved(DeviceInfo deviceInfoEvent);
}
