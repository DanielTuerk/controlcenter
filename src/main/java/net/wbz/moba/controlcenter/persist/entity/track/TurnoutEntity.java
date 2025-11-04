package net.wbz.moba.controlcenter.persist.entity.track;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;



import net.wbz.moba.controlcenter.shared.track.model.AbstractTrackPart;
import net.wbz.moba.controlcenter.shared.track.model.Turnout;

/**
 * Entity for a two-way turnout.
 *
 * @author Daniel Tuerk
 */
@Entity
@Table(name = "TRACKPART_SWITCH")
public class TurnoutEntity extends AbstractTrackPartEntity implements HasToggleFunctionEntity {

    @Enumerated(EnumType.ORDINAL)
    public Turnout.DIRECTION currentDirection;

    @Enumerated(EnumType.ORDINAL)
    public Turnout.PRESENTATION currentPresentation;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    public BusDataConfigurationEntity toggleFunction;
    /**
     * BusDataConfigurationEntity to toggle the {@link AbstractTrackPartEntity} by an event.
     */
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    public EventConfigurationEntity eventConfiguration;

    @Override
    public EventConfigurationEntity getEventConfiguration() {
        return eventConfiguration;
    }

    @Override
    public void setEventConfiguration(EventConfigurationEntity eventConfigurationEntity) {
        eventConfiguration = eventConfigurationEntity;
    }

}
