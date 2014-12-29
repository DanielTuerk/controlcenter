package net.wbz.moba.controlcenter.web.shared.bus;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
public class DeviceInfo implements IsSerializable {

    private boolean connected;

    public enum DEVICE_TYPE {SERIAL, TEST}

    private String key;
    private DEVICE_TYPE type;

    public String getKey() {
        return key;
    }

    public DEVICE_TYPE getType() {
        return type;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setType(DEVICE_TYPE type) {
        this.type = type;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public boolean isConnected() {
        return connected;
    }
}
