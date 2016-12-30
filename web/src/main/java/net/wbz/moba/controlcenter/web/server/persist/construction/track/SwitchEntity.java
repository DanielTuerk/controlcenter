package net.wbz.moba.controlcenter.web.server.persist.construction.track;

import com.googlecode.jmapper.annotations.JMap;
import net.wbz.moba.controlcenter.web.shared.track.model.AbstractTrackPart;
import net.wbz.moba.controlcenter.web.shared.track.model.Switch;

import javax.persistence.*;

/**
 * @author Daniel Tuerk
 */
@Entity
@Table(name = "trackpart_switch")
public class SwitchEntity extends AbstractTrackPartEntity implements HasToggleFunctionEntity {

    @JMap
    @Column
    private Switch.DIRECTION currentDirection;

    @JMap
    @Column
    private Switch.PRESENTATION currentPresentation;

    @JMap
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private BusDataConfigurationEntity toggleFunction;
    /**
     * BusDataConfigurationEntity to toggle the {@link AbstractTrackPartEntity} by an event.
     */
    @JMap
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private EventConfigurationEntity eventConfiguration;

    public EventConfigurationEntity getEventConfiguration() {
        return eventConfiguration;
    }

    public void setEventConfiguration(EventConfigurationEntity eventConfigurationEntity) {
        eventConfiguration = eventConfigurationEntity;
    }


    public BusDataConfigurationEntity getToggleFunction() {
        return toggleFunction;
    }

    public void setToggleFunction(BusDataConfigurationEntity toggleFunction) {
        this.toggleFunction = toggleFunction;
    }

    public void setCurrentDirection(Switch.DIRECTION currentDirection) {
        this.currentDirection = currentDirection;
    }

    public void setCurrentPresentation(Switch.PRESENTATION currentPresentation) {
        this.currentPresentation = currentPresentation;
    }

    public Switch.PRESENTATION getCurrentPresentation() {
        return currentPresentation;
    }

    public Switch.DIRECTION getCurrentDirection() {
        return currentDirection;
    }


    @Override
    public Class<? extends AbstractTrackPart> getDefaultDtoClass() {
        return Switch.class;
    }
}