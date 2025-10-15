package net.wbz.moba.controlcenter.persist.entity.track;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * @author Daniel Tuerk
 */
@Entity
@Table(name = "TRACKPART_UNCOUPLER")
public class UncouplerEntity extends StraightEntity implements HasToggleFunctionEntity {

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    public BusDataConfigurationEntity toggleFunction;
    /**
     * BusDataConfigurationEntity to toggle the {@link AbstractTrackPartEntity} by an event.
     */
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    public EventConfigurationEntity eventConfiguration;

    public EventConfigurationEntity getEventConfiguration() {
        return eventConfiguration;
    }

    public void setEventConfiguration(EventConfigurationEntity eventConfigurationEntity) {
        eventConfiguration = eventConfigurationEntity;
    }

}
