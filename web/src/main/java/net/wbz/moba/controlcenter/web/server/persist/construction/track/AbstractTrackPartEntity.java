package net.wbz.moba.controlcenter.web.server.persist.construction.track;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import com.google.common.collect.Sets;
import com.googlecode.jmapper.annotations.JMap;

import net.wbz.moba.controlcenter.web.server.persist.AbstractEntity;
import net.wbz.moba.controlcenter.web.server.persist.construction.ConstructionEntity;
import net.wbz.moba.controlcenter.web.shared.track.model.AbstractTrackPart;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * Model for a part of the track.
 * - has position in grid
 * - provide functions
 *
 * @author Daniel Tuerk
 */
@Entity(name = "TRACK_PART")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class AbstractTrackPartEntity extends AbstractEntity {

    /**
     * The corresponding construction.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private ConstructionEntity construction;

    /**
     * Position of the track part in the grid system of the construction.
     */
    @JMap
    @OneToOne(fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
    @OnDelete(action = OnDeleteAction.CASCADE)
    private GridPositionEntity gridPosition;

    @JMap
    @ManyToOne(fetch = FetchType.EAGER)
    private TrackBlockEntity trackBlock;

    public AbstractTrackPartEntity() {
    }

    public ConstructionEntity getConstruction() {
        return construction;
    }

    public void setConstruction(ConstructionEntity construction) {
        this.construction = construction;
    }

    public List<TrackPartFunctionEntity> getFunctionConfigs() {
        // TODO drop?
        return Collections.emptyList();
    }

    public Set<BusDataConfigurationEntity> getConfigurationsOfFunctions() {
        Set<BusDataConfigurationEntity> configurations = Sets.newHashSet();
        for (TrackPartFunctionEntity function : getFunctionConfigs()) {
            configurations.add(function.getConfiguration());
        }
        return configurations;
    }

    public GridPositionEntity getGridPosition() {
        return gridPosition;
    }

    public void setGridPosition(GridPositionEntity gridPositionEntity) {
        this.gridPosition = gridPositionEntity;
    }

    public TrackBlockEntity getTrackBlock() {
        return trackBlock;
    }

    public void setTrackBlock(TrackBlockEntity trackBlock) {
        this.trackBlock = trackBlock;
    }

    public abstract Class<? extends AbstractTrackPart> getDefaultDtoClass();
}
