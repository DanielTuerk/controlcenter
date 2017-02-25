package net.wbz.moba.controlcenter.web.server.persist.construction.track;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.google.common.base.Objects;
import com.googlecode.jmapper.annotations.JMap;

import net.wbz.moba.controlcenter.web.server.persist.AbstractEntity;

/**
 * BusDataConfigurationEntity model of an function for the {@link AbstractTrackPartEntity}.
 *
 * @author Daniel Tuerk
 */
@Entity(name = "BUSDATA_CONFIG")
public class BusDataConfigurationEntity extends AbstractEntity {

    @JMap
    @Column(name = "CONFIG_BUS")
    private Integer bus;

    @JMap
    @Column(name = "CONFIG_ADDRESS")
    private Integer address;

    @JMap
    @Column(name = "CONFIG_BIT")
    private Integer bit;

    @JMap
    @Column(name = "CONFIG_BIT_STATE", columnDefinition = "int default 1")
    private Boolean bitState;

    public BusDataConfigurationEntity(int bus, int address, int bit, boolean bitState) {
        this.bus = bus;
        this.address = address;
        this.bit = bit;
        this.bitState = bitState;
    }

    public BusDataConfigurationEntity() {
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
        return "BusDataConfigurationEntity{" +
                "bus=" + bus +
                ", address=" + address +
                ", bit=" + bit +
                ", bitState=" + bitState +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        BusDataConfigurationEntity that = (BusDataConfigurationEntity) o;
        return Objects.equal(bus, that.bus) &&
                Objects.equal(address, that.address) &&
                Objects.equal(bit, that.bit);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(bus, address, bit);
    }
}
