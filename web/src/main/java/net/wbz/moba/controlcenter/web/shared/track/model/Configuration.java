package net.wbz.moba.controlcenter.web.shared.track.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * Configuration model of an function for the {@link net.wbz.moba.controlcenter.web.shared.track.model.TrackPart}.
 *
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
@Entity
public class Configuration implements Serializable {

    @Id
    @GeneratedValue
    private long id;

    @Column(name = "config_bus")
    private int bus;

    @Column(name = "config_address")
    private int address;

    @Column(name = "config_bit")
    private int bit;

    private boolean bitState;

    public Configuration(int bus, int address, int bit, boolean bitState) {
        this.bus = bus;
        this.address = address;
        this.bit = bit;
        this.bitState = bitState;
    }

    public Configuration() {
    }

    public void setId(long id) {
        this.id = id;
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
        return "Configuration{" +
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

        Configuration that = (Configuration) o;

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

    public long getId() {
        return id;
    }

}
