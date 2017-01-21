package net.wbz.moba.controlcenter.web.server.persist.construction.track;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.googlecode.jmapper.annotations.JMap;

import net.wbz.moba.controlcenter.web.shared.track.model.AbstractTrackPart;
import net.wbz.moba.controlcenter.web.shared.track.model.Uncoupler;

/**
 * @author Daniel Tuerk
 */
@Entity
@Table(name = "TRACKPART_UNCOUPLER")
public class UncouplerEntity extends StraightEntity implements HasToggleFunctionEntity {

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

    @Override
    public Class<? extends AbstractTrackPart> getDefaultDtoClass() {
        return Uncoupler.class;
    }

}
