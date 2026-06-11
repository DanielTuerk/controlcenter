package net.wbz.moba.controlcenter.shared.scenario;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.wbz.moba.controlcenter.shared.track.model.AbstractDto;
import net.wbz.moba.controlcenter.shared.train.Train;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Daniel Tuerk
 */
@Setter
@Getter
public class Scenario extends AbstractDto {

    /**
     * State of the actual execution.
     */
    public enum RUN_STATE {
        /**
         * Scenario wasn't executed.
         */
        NONE,
        /**
         * Scheduled execution waiting for cron.
         */
        SCHEDULED,
        /**
         * Scenario is currently running.
         */
        RUNNING,
        /**
         * TODO
         */
        PAUSED,
        /**
         * Scenario is stopped.
         */
        STOPPED,
        /**
         * Scenario successfully executed.
         */
        SUCCESS,
        /**
         * Scenario executed with error.
         */
        FAILED
    }
    /**
     * Mode of the scenario execution.
     */
    public enum MODE {
        /**
         * Scenario is inactive.
         */
        OFF,
        /**
         * Scenario started manually for a single execution.
         */
        MANUAL,
        /**
         * Scenario is in automatic mode to execute on trigger by configured cron.
         */
        AUTOMATIC
    }
    private String name;
    private String cron;
    /**
     * TODO also looking for position on track - drive to possible start point, or resume on actual pos TODO check
     * existing on track
     */
    private Train train;
    private Train.DRIVING_DIRECTION trainDrivingDirection;
    private Integer startDrivingLevel;
    @Getter(AccessLevel.NONE)
    private List<RouteSequence> routeSequences;
    private Long stationPlatformStartId;
    private Long stationPlatformEndId;
    // TODO not persisted, needed here?

    public List<RouteSequence> getRouteSequences() {
        if (routeSequences == null) {
            routeSequences = new ArrayList<>();
        }
        return routeSequences;
    }

    @Override
    public String toString() {
        return "Scenario{" + "name='" + name + '\'' +
            ", cron='" + cron + '\'' +
            ", train=" + train +
            '}';
    }

}
