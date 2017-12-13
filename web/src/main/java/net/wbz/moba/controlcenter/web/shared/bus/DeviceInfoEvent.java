package net.wbz.moba.controlcenter.web.shared.bus;

import com.google.common.base.Objects;

import de.novanic.eventservice.client.event.Event;

/**
 * @author Daniel Tuerk
 */
public class DeviceInfoEvent implements Event {

    private DeviceInfo deviceInfo;
    private TYPE eventType;

    public DeviceInfoEvent() {
    }

    public DeviceInfoEvent(DeviceInfo deviceInfo, TYPE eventType) {
        this.deviceInfo = deviceInfo;
        this.eventType = eventType;
    }

    public DeviceInfo getDeviceInfo() {
        return deviceInfo;
    }

    public TYPE getEventType() {
        return eventType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DeviceInfoEvent that = (DeviceInfoEvent) o;
        return Objects.equal(deviceInfo, that.deviceInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(deviceInfo);
    }

    public enum TYPE {
        CREATE, REMOVE, MODIFY, CONNECTED, DISCONNECTED
    }
}
