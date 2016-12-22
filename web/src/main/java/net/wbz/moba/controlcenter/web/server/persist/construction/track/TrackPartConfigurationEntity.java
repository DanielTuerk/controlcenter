package net.wbz.moba.controlcenter.web.server.persist.construction.track;


import com.googlecode.jmapper.annotations.JMap;
import net.wbz.moba.controlcenter.web.server.persist.AbstractEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * TrackPartConfigurationEntity model of an function for the {@link TrackPartEntity}.
 *
 * @author Daniel Tuerk
 */
@Entity
public class TrackPartConfigurationEntity extends AbstractEntity {

    @JMap
    @Column(name = "config_bus")
    private int bus;

    @JMap
    @Column(name = "config_address")
    private int address;

    @JMap
    @Column(name = "config_bit")
    private int bit;

    private boolean bitState;

    public TrackPartConfigurationEntity(int bus, int address, int bit, boolean bitState) {
        this.bus = bus;
        this.address = address;
        this.bit = bit;
        this.bitState = bitState;
    }

    public TrackPartConfigurationEntity() {
    }


    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public int getBit() {
        return bit;
    }

    public void setBit(int bit) {
        this.bit = bit;
    }

    public boolean isValid() {
        return address > 0 && bit > 0 && bus > -1;
    }

    public boolean isBitState() {
        return bitState;
    }

    public void setBitState(boolean bitState) {
        this.bitState = bitState;
    }

    public int getBus() {
        return bus;
    }

    public void setBus(int bus) {
        this.bus = bus;
    }

    @Override
    public String toString() {
        return "TrackPartConfigurationEntity{" +
                "bus=" + bus +
                ", address=" + address +
                ", bit=" + bit +
                ", bitState=" + bitState +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TrackPartConfigurationEntity that = (TrackPartConfigurationEntity) o;

        if (address != that.address) return false;
        if (bit != that.bit) return false;
        if (bus != that.bus) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = bus;
        result = 31 * result + address;
        result = 31 * result + bit;
        return result;
    }


}
