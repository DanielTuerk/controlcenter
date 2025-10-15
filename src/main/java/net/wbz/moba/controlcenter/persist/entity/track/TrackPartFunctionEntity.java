package net.wbz.moba.controlcenter.persist.entity.track;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import net.wbz.moba.controlcenter.persist.entity.AbstractEntity;


/**
 * @author Daniel Tuerk
 */
@Entity
@Table(name = "TRACKPART_FUNCTION")
public class TrackPartFunctionEntity extends AbstractEntity {

    public String functionKey;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    public BusDataConfigurationEntity configuration;


    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        TrackPartFunctionEntity that = (TrackPartFunctionEntity) o;

        return functionKey != null ? functionKey.equals(that.functionKey) : that.functionKey == null;

    }

    @Override
    public int hashCode() {
        return functionKey != null ? functionKey.hashCode() : 0;
    }

}
