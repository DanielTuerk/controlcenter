package net.wbz.moba.controlcenter.persist.entity.track;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import net.wbz.moba.controlcenter.persist.entity.AbstractEntity;


/**
 * @author Daniel Tuerk
 */
@Entity
@Table(name = "EVENT_CONFIG")
public class EventConfigurationEntity extends AbstractEntity {

    @ManyToOne
    public BusDataConfigurationEntity stateOnConfig;

    @ManyToOne
    public BusDataConfigurationEntity stateOffConfig;

    public boolean isActive() {
        return stateOnConfig != null && stateOffConfig != null;
    }

}
