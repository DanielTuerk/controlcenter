package net.wbz.moba.controlcenter.web.server.persist.scenario;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.googlecode.jmapper.annotations.JMap;

import net.wbz.moba.controlcenter.web.server.persist.AbstractEntity;
import net.wbz.moba.controlcenter.web.server.persist.train.TrainEntity;
import net.wbz.moba.controlcenter.web.shared.train.Train;

/**
 * @author Daniel Tuerk
 */
@Entity(name = "SCENARIO")
public class ScenarioEntity extends AbstractEntity {

    @JMap
    @Column
    private String name;

    @JMap
    @Column
    private String cron;

    /**
     * TODO also looking for position on track - drive to possible start point, or resume on actual pos
     * TODO check existing on track
     */
    @JMap
    @ManyToOne
    private TrainEntity train;

    /**
     * Driving direction to set for the train at scenario start on the route.
     */
    @JMap
    @Column
    private Train.DRIVING_DIRECTION trainDrivingDirection;

    /**
     * Driving level to start the train for scenario start on the route.
     */
    @JMap
    @Column
    private Integer startDrivingLevel;

    /**
     * Route to drive from start to end station.
     * TODO: interstations aren't supported yet
     */
    @JMap
    @OneToMany(mappedBy = "scenario", fetch = FetchType.EAGER)
    private List<RouteSequenceEntity> routeSequences;

    @JMap
    private Long stationPlatformStartId;

    @JMap
    private Long stationPlatformEndId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public TrainEntity getTrain() {
        return train;
    }

    public void setTrain(TrainEntity train) {
        this.train = train;
    }

    public List<RouteSequenceEntity> getRouteSequences() {
        return routeSequences;
    }

    public void setRouteSequences(
            List<RouteSequenceEntity> routeSequences) {
        this.routeSequences = routeSequences;
    }

    public Train.DRIVING_DIRECTION getTrainDrivingDirection() {
        return trainDrivingDirection;
    }

    public void setTrainDrivingDirection(Train.DRIVING_DIRECTION trainDrivingDirection) {
        this.trainDrivingDirection = trainDrivingDirection;
    }

    public Integer getStartDrivingLevel() {
        return startDrivingLevel;
    }

    public void setStartDrivingLevel(Integer startDrivingLevel) {
        this.startDrivingLevel = startDrivingLevel;
    }

    public Long getStationPlatformStartId() {
        return stationPlatformStartId;
    }

    public void setStationPlatformStartId(Long stationPlatformStartId) {
        this.stationPlatformStartId = stationPlatformStartId;
    }

    public Long getStationPlatformEndId() {
        return stationPlatformEndId;
    }

    public void setStationPlatformEndId(Long stationPlatformEndId) {
        this.stationPlatformEndId = stationPlatformEndId;
    }
}
