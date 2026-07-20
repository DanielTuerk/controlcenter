package io.github.danieltuerk.controlcenter.persist.entity.track;

import io.github.danieltuerk.controlcenter.persist.entity.AbstractEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


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
