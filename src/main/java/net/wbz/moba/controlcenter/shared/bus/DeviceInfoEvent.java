package net.wbz.moba.controlcenter.shared.bus;

import net.wbz.moba.controlcenter.shared.Event;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * Event for the data of the device.
 *
 * @author Daniel Tuerk
 */
@Schema(description = "bus status update sent via WebSocket")
@Tag(ref = "websocket")
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
        return java.util.Objects.equals(deviceInfo, that.deviceInfo) && eventType == that.eventType;
    }

    @Override
    public int hashCode() {

        return java.util.Objects.hash(deviceInfo, eventType);
    }

    @Override
    public String toString() {
        return "DeviceInfoEvent{" + "deviceInfo=" + deviceInfo
            + ", eventType=" + eventType
            + '}';
    }

    public enum TYPE {
        CREATE, REMOVE, MODIFY
    }
}
