package net.wbz.moba.controlcenter.web.shared.bus;

import net.wbz.moba.controlcenter.web.shared.Identity;

import javax.persistence.*;

/**
 * @author Daniel Tuerk
 */
@Entity
public class DeviceInfo implements Identity {

    @Transient
    private boolean connected;

    @Id
    @GeneratedValue
    @Column(name = "device_id")
    private long id;

    @Column(name = "device_key")
    private String key;
    private DEVICE_TYPE type;

    @Override
    public Integer getVersion() {
        return 0;
    }

    @Override
    public Long getId() {
        return id;
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
                ", id=" + id +
                ", key='" + key + '\'' +
                ", type=" + type +
                '}';
    }

    public enum DEVICE_TYPE {
        SERIAL, TEST
    }
}
