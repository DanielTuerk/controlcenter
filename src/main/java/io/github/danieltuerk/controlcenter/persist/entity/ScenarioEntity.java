package io.github.danieltuerk.controlcenter.persist.entity;

import io.github.danieltuerk.controlcenter.shared.train.Train;
import jakarta.persistence.*;

import java.util.List;

/**
 * @author Daniel Tuerk
 */
@Entity
@Table(name = "SCENARIO")
public class ScenarioEntity extends AbstractEntity {

    public String name;

    public String cron;

    @ManyToOne
    public ConstructionEntity construction;

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

    @OneToMany(mappedBy = "scenario", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @OrderBy("position ASC")
    public List<RouteSequenceEntity> routeSequences;

    public Long stationPlatformStartId;

    public Long stationPlatformEndId;

}
