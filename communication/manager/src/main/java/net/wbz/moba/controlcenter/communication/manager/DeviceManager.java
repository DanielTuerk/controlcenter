package net.wbz.moba.controlcenter.communication.manager;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.wbz.moba.controlcenter.communication.api.Device;
import net.wbz.moba.controlcenter.communication.api.DeviceAccessException;
import net.wbz.moba.controlcenter.communication.api.DeviceConnectionListener;
import net.wbz.moba.controlcenter.communication.serial.SerialDevice;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
public class DeviceManager {

    private final Map<String, Device> devices = Maps.newHashMap();

    public enum DEVICE_TYPE {COM1}

    public Device registerDevice(DEVICE_TYPE type, String deviceId) {
        if (!devices.containsKey(deviceId)) {
            Device device = createDevice(type, deviceId);
            devices.put(deviceId, device);
        }
        return devices.get(deviceId);
    }

    private Device createDevice(DEVICE_TYPE type, String deviceId) {
        switch (type) {
            case COM1:
                return new SerialDevice(deviceId);
            default:
                throw new RuntimeException("no device found for type " + type.name());
        }
    }

    public String getDeviceId(Device device) {
        if (devices.containsValue(device)) {
            for (Map.Entry<String, Device> entry : devices.entrySet()) {
                if (entry.getValue() == device) {
                    return entry.getKey();
                }
            }
        }
        throw new RuntimeException("no key for value");
    }

    public List<Device> getDevices() {
        return Lists.newArrayList(devices.values());
    }

    public Device getDeviceById(String deviceId) {
        return devices.get(deviceId);
    }

    public List<String> getDeviceIds() {
        return Lists.newArrayList(devices.keySet());
    }

    public Device getConnectedDevice() throws DeviceAccessException {
        for (Device device : devices.values()) {
            if (device.isConnected()) {
                return device;
            }
        }
        throw new DeviceAccessException("no device connected");
    }

    public void removeDevice(Device device) {
        for (Map.Entry<String, Device> entry : devices.entrySet()) {
            if (entry.getValue() == device) {
                devices.remove(entry.getKey());
                return;
            }
        }
        throw new RuntimeException("no device found to delete");
    }

    public void addDeviceConnectionListener(DeviceConnectionListener listener) {
        for(Device device : devices.values()) {
            device.addDeviceConnectionListener(listener);
        }
    }

    public void removeDeviceConnectionListener(DeviceConnectionListener listener) {
        for(Device device : devices.values()) {
            device.removeDeviceConnectionListener(listener);
        }
    }

}