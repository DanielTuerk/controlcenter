package net.wbz.moba.controlcenter.web.shared.bus;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import net.wbz.moba.controlcenter.web.shared.HasVersionAndId;

/**
 * @author Daniel Tuerk
 */
@Entity
public class DeviceInfo implements HasVersionAndId {

    @Transient
    private boolean connected;
    @Id
    @Column(name = "device_key")
    private String key;
    private DEVICE_TYPE type;

    @Override
    public Integer getVersion() {
        return 0;
    };

    @Override
    public Long getId() {
        // TODO
        return (long) key.hashCode();
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public DEVICE_TYPE getType() {
        return type;
    }

    public void setType(DEVICE_TYPE type) {
        this.type = type;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    @Override
    public String toString() {
        return "DeviceInfo{" +
                "connected=" + connected +
                ", key='" + key + '\'' +
                ", type=" + type.name() +
                '}';
    }

    public enum DEVICE_TYPE {
        SERIAL, TEST
    }
}
