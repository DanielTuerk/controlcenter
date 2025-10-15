package net.wbz.moba.controlcenter.web.server.persist.device;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;





/**
 * @author Daniel Tuerk
 */
@Entity
@Table(name = "DEVICE_INFO")
public class DeviceInfoEntity extends AbstractEntity {

    public enum DEVICE_TYPE {
        SERIAL, TEST
    }

    @Transient
    private boolean connected;

    @Column(name = "device_key")
    private String key;
    private DEVICE_TYPE type;

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
        return "DeviceInfoEntity{" +
                "connected=" + connected +
                ", id=" + getId() +
                ", key='" + key + '\'' +
                ", type=" + type +
                '}';
    }

}
