package io.github.danieltuerk.controlcenter.shared.scenario;

import io.github.danieltuerk.controlcenter.shared.track.model.AbstractDto;
import io.github.danieltuerk.controlcenter.shared.train.Train;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    private String name;
    private String cron;
    private Train train;
    private Train.DRIVING_DIRECTION trainDrivingDirection;
    private Integer startDrivingLevel;
    @Getter(AccessLevel.NONE)
    private List<RouteSequence> routeSequences;

    public List<RouteSequence> getRouteSequences() {
        if (routeSequences == null) {
            routeSequences = new ArrayList<>();
        }
        return routeSequences;
    }

    @Override
    public String toString() {
        return "Scenario{" +
            "id=" + getId() + '\'' +
            ", name='" + name + '\'' +
            ", cron='" + cron + '\'' +
            ", train=" + train +
            ", trainDrivingDirection=" + trainDrivingDirection +
            ", startDrivingLevel=" + startDrivingLevel +
            ", routeSequences=" + routeSequences.stream().map(RouteSequence::getId).collect(Collectors.toSet()) +
            '}';
    }
}
