package net.wbz.moba.controlcenter.web.shared.bus;

import de.novanic.eventservice.client.event.Event;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
public class DeviceInfoEvent implements Event {

    public enum TYPE {CREATE, REMOVE, MODIFY}

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
}
