package net.wbz.moba.controlcenter.shared.track.model;

import com.google.common.base.Objects;


/**
 * @author Daniel Tuerk
 */
public class BusDataConfiguration extends AbstractDto {

    private Integer bus;

    private Integer address;

    private Integer bit;

    private Boolean bitState;

    public BusDataConfiguration(Integer bus, Integer address, Integer bit, Boolean bitState) {
        this.bus = bus;
        this.address = address;
        this.bit = bit;
        setBitState(bitState);
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
        this.bitState = bitState == null ? true : bitState;
    }

    public boolean isValid() {
        return (bus != null && address != null && bit != null) && address > 0 && bit > 0 && bus > -1;
    }

    public String getIdentifierKey() {
        return address + "-" + bit;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("BusDataConfiguration{");
        sb.append("bus=").append(bus);
        sb.append(", address=").append(address);
        sb.append(", bit=").append(bit);
        sb.append(", bitState=").append(bitState);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BusDataConfiguration that = (BusDataConfiguration) o;
        return java.util.Objects.equals(bus, that.bus) && java.util.Objects.equals(address, that.address)
            && java.util.Objects.equals(bit, that.bit);
    }

    @Override
    public int hashCode() {
        // TODO drop super?
        return java.util.Objects.hash(super.hashCode(), bus, address, bit);
    }

    /**
     * Check that the given object has the same configuration for bus, address and bit.
     *
     * @param that {@link BusDataConfiguration} to compare
     * @return {@code true} for same config on bus, address and bit
     */
    public boolean isSameConfig(BusDataConfiguration that) {
        return Objects.equal(getBus(), that.getBus()) && Objects.equal(getAddress(), that.getAddress()) && Objects
            .equal(getBit(), that.getBit());
    }
}
