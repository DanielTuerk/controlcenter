package net.wbz.moba.controlcenter.persist.entity.track;

import com.google.common.base.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import net.wbz.moba.controlcenter.persist.entity.AbstractEntity;


/**
 * BusDataConfigurationEntity model of an function for the {@link AbstractTrackPartEntity}.
 *
 * @author Daniel Tuerk
 */
@Entity
@Table(name = "BUSDATA_CONFIG")
public class BusDataConfigurationEntity extends AbstractEntity {

    @Column(name = "CONFIG_BUS")
    public Integer bus;

    @Column(name = "CONFIG_ADDRESS")
    public Integer address;

    @Column(name = "CONFIG_BIT")
    public Integer bit;

    @Column(name = "CONFIG_BIT_STATE")
    public Boolean bitState;

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
