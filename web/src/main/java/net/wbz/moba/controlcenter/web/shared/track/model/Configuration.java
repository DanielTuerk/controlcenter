package net.wbz.moba.controlcenter.web.shared.track.model;

import java.io.Serializable;

/**
 * Configuration model of an function for the {@link net.wbz.moba.controlcenter.web.shared.track.model.TrackPart}.
 *
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
public class Configuration implements Serializable {

    private int bus;

    private int address;

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
        return address > -1 && bit > 0 && bus >-1;
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
        if (bitState != that.bitState) return false;
        if (bus != that.bus) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = bus;
        result = 31 * result + address;
        result = 31 * result + bit;
        result = 31 * result + (bitState ? 1 : 0);
        return result;
    }
}
