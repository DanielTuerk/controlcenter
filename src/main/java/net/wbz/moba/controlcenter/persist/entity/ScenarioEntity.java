package net.wbz.moba.controlcenter.persist.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import net.wbz.moba.controlcenter.shared.train.Train;

/**
 * @author Daniel Tuerk
 */
@Entity
@Table(name = "SCENARIO")
public class ScenarioEntity extends AbstractEntity {

    public String name;

    public String cron;

    /**
     * TODO also looking for position on track - drive to possible start point, or resume on actual pos
     * TODO check existing on track
     */
    @ManyToOne
    public TrainEntity train;

    /**
     * Driving direction to set for the train at scenario start on the route.
     */
    public Train.DRIVING_DIRECTION trainDrivingDirection;

    /**
     * Driving level to start the train for scenario start on the route.
     */
    public Integer startDrivingLevel;

    /**
     * Route to drive from start to end station.
     * TODO: interstations aren't supported yet
     */
    @OneToMany(mappedBy = "scenario", fetch = FetchType.EAGER)
    public List<RouteSequenceEntity> routeSequences;

    public Long stationPlatformStartId;

    public Long stationPlatformEndId;

}
