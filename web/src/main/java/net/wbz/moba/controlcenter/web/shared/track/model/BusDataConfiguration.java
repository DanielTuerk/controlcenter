package net.wbz.moba.controlcenter.web.shared.track.model;

import com.google.common.base.Objects;
import com.googlecode.jmapper.annotations.JMap;

/**
 * @author Daniel Tuerk
 */
public class BusDataConfiguration extends AbstractDto {
    @JMap
    private int bus;

    @JMap
    private int address;

    @JMap
    private int bit;

    @JMap
    private boolean bitState;

    public BusDataConfiguration(int bus, int address, int bit, boolean bitState) {
        this.bus = bus;
        this.address = address;
        this.bit = bit;
        this.bitState = bitState;
    }

    public BusDataConfiguration() {
    }

    public int getBus() {
        return bus;
    }

    public void setBus(int bus) {
        this.bus = bus;
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

    public boolean isBitState() {
        return bitState;
    }

    public void setBitState(boolean bitState) {
        this.bitState = bitState;
    }

    public boolean isValid() {
        return address > 0 && bit > 0 && bus > -1;
    }

    @Override
    public String toString() {
        return "BusDataConfiguration{" +
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
        BusDataConfiguration that = (BusDataConfiguration) o;
        return bus == that.bus &&
                address == that.address &&
                bit == that.bit;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(bus, address, bit);
    }
}
