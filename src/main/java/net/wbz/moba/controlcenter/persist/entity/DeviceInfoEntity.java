package net.wbz.moba.controlcenter.persist.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * @author Daniel Tuerk
 */
@Entity
@Table(name = "DEVICE_INFO")
public class DeviceInfoEntity extends AbstractEntity {

    public enum DEVICE_TYPE {
        SERIAL, TEST
    }

//    @Transient
//    private boolean connected;

    @Column(name = "device_key")
    public String key;
    
    public DEVICE_TYPE type;

    @Override
    public String toString() {
        return "DeviceInfoEntity{" +
            ", id=" + super.id +
            ", key='" + key + '\'' +
            ", type=" + type +
            '}';
    }

}
