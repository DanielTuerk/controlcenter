package net.wbz.moba.controlcenter.persist.entity;

import jakarta.persistence.Table;
import java.util.List;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import net.wbz.moba.controlcenter.persist.entity.track.BlockStraightEntity;
import net.wbz.moba.controlcenter.persist.entity.track.GridPositionEntity;
import net.wbz.moba.controlcenter.persist.entity.track.TrackBlockEntity;

/**
 * TODO station und stopover sind sehr ähnlich, hier wird auch was abstraktes für start und ziel gebraucht
 *
 * @author Daniel Tuerk
 */
@Entity
@Table(name = "SCENARIO_ROUTE")
public class RouteEntity extends AbstractEntity {

    /**
     * TODO validations
     */
    public String name;

    @ManyToOne
    public BlockStraightEntity start;

    @ManyToOne
    public TrackBlockEntity end;

    /**
     * Indicate one-way track or multi-ways. On one-way only one train can drive from start to end. No other train can
     * go on a route with the same start and end and have to wait until the track is clear.
     * TODO how to handle different ways on different station rails in one station?
     */
    @Column
    public Boolean oneway;

    /**
     * Optional waypoints between the start and end point.
     */
    @ManyToMany(fetch = FetchType.EAGER)
    public List<GridPositionEntity> waypoints;

}
