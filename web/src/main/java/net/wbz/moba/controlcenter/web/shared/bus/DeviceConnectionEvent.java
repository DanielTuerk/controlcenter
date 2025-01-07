package net.wbz.moba.controlcenter.web.shared.bus;

import net.wbz.moba.controlcenter.web.client.event.StateEvent;

/**
 * Event for the connection state of a device.
 *
 * @author Daniel Tuerk
 */
public class DeviceConnectionEvent implements StateEvent {

    public enum TYPE {
        CONNECTED, DISCONNECTED
    }

    private DeviceInfo deviceInfo;
    private TYPE eventType;

    public DeviceConnectionEvent() {
    }

    public DeviceConnectionEvent(DeviceInfo deviceInfo, TYPE eventType) {
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
    public String getCacheKey() {
        return getClass().getSimpleName() + ":" + deviceInfo.getKey();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DeviceConnectionEvent that = (DeviceConnectionEvent) o;
        return java.util.Objects.equals(deviceInfo, that.deviceInfo) && eventType == that.eventType;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(deviceInfo, eventType);
    }

    @Override
    public String toString() {
        return "DeviceConnectionEvent{" + "deviceInfo=" + deviceInfo
            + ", eventType=" + eventType
            + '}';
    }
}
