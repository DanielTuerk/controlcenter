package net.wbz.moba.controlcenter.web.shared.track.model;

import com.google.common.base.Objects;
import com.googlecode.jmapper.annotations.JMap;

/**
 * @author Daniel Tuerk
 */
public class BusDataConfiguration extends AbstractDto {
    @JMap
    private Integer bus;

    @JMap
    private Integer address;

    @JMap
    private Integer bit;

    @JMap
    private Boolean bitState;

    public BusDataConfiguration(Integer bus, Integer address, Integer bit, Boolean bitState) {
        this.bus = bus;
        this.address = address;
        this.bit = bit;
        this.bitState = bitState;
    }

    public BusDataConfiguration() {
    }

    public Integer getBus() {
        return bus;
    }

    public void setBus(Integer bus) {
        this.bus = bus;
    }

    public Integer getAddress() {
        return address;
    }

    public void setAddress(Integer address) {
        this.address = address;
    }

    public Integer getBit() {
        return bit;
    }

    public void setBit(Integer bit) {
        this.bit = bit;
    }

    public Boolean getBitState() {
        return bitState;
    }

    public void setBitState(Boolean bitState) {
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
        if (!super.equals(o)) return false;
        BusDataConfiguration that = (BusDataConfiguration) o;
        return Objects.equal(bus, that.bus) &&
                Objects.equal(address, that.address) &&
                Objects.equal(bit, that.bit);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), bus, address, bit);
    }
}
