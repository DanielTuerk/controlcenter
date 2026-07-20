package io.github.danieltuerk.controlcenter.persist.entity.track;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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
    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    public EventConfigurationEntity eventConfiguration;

    @Override
    public BusDataConfigurationEntity getToggleFunctionConfiguration() {
        return toggleFunction;
    }

    @Override
    public void setToggleFunctionConfiguration(BusDataConfigurationEntity busDataConfigurationEntity) {
        this.toggleFunction = busDataConfigurationEntity;
    }

}
