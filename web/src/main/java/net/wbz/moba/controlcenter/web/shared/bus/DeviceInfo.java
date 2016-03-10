package net.wbz.moba.controlcenter.web.shared.bus;

import com.google.gwt.user.client.rpc.IsSerializable;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

/**
 * @author Daniel Tuerk
 */
@Entity
public class DeviceInfo implements IsSerializable {

    @Transient
    private boolean connected;

    public enum DEVICE_TYPE {SERIAL, TEST}

    @Id
    @Column(name = "device_key")
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

    @Override
    public String toString() {
        return "DeviceInfo{" +
                "connected=" + connected +
                ", key='" + key + '\'' +
                ", type=" + type.name() +
                '}';
    }
}
