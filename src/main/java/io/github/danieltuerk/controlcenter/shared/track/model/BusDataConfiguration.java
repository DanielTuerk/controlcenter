package io.github.danieltuerk.controlcenter.shared.track.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;


/**
 * @author Daniel Tuerk
 */
public class BusDataConfiguration extends AbstractDto {

    @Setter
    @Getter
    private Integer bus;

    @Setter
    @Getter
    private Integer address;

    @Setter
    @Getter
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

    public boolean getBitState() {
        return bitState != null && bitState;
    }

    public void setBitState(Boolean bitState) {
        this.bitState = bitState == null || bitState;
    }

    @JsonIgnore
    public boolean isValid() {
        return (bus != null && address != null && bit != null) && address > 0 && bit > 0 && bus > -1;
    }

    @JsonIgnore
    public String getIdentifierKey() {
        return address + "-" + bit;
    }

    @Override
    public String toString() {
        return "BusDataConfiguration{bus=%d, address=%d, bit=%d, bitState=%s}".formatted(bus, address, bit, bitState);
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
        return java.util.Objects.hash(super.hashCode(), bus, address, bit);
    }

    /**
     * Check that the given object has the same configuration for bus, address and bit.
     *
     * @param that {@link BusDataConfiguration} to compare
     * @return {@code true} for same config on bus, address and bit
     */
    public boolean isSameConfig(BusDataConfiguration that) {
        return Objects.equals(getBus(), that.getBus()) && Objects.equals(getAddress(), that.getAddress()) && Objects
            .equals(getBit(), that.getBit());
    }
}
