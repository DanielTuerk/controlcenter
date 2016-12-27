package net.wbz.moba.controlcenter.web.server.persist.device;

import com.googlecode.jmapper.annotations.JMap;
import net.wbz.moba.controlcenter.web.server.persist.AbstractEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Transient;

/**
 * @author Daniel Tuerk
 */
@Entity(name = "device_info")
public class DeviceInfoEntity extends AbstractEntity {

    public enum DEVICE_TYPE {
        SERIAL, TEST
    }


    @Transient
    private boolean connected;

    @Column(name = "device_key")
    @JMap
    private String key;
    @JMap
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
