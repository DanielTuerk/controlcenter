package net.wbz.moba.controlcenter.web.shared.scenario;

import java.util.List;

import com.google.common.base.Optional;
import com.googlecode.jmapper.annotations.JMap;

import net.wbz.moba.controlcenter.web.shared.track.model.AbstractDto;
import net.wbz.moba.controlcenter.web.shared.track.model.Signal;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackBlock;
import net.wbz.moba.controlcenter.web.shared.train.Train;

/**
 * @author Daniel Tuerk
 */
public class Scenario extends AbstractDto {

    @JMap
    private String name;

    @JMap
    private String cron;

    /**
     * TODO also looking for position on track - drive to possible start point, or resume on actual pos
     * TODO check existing on track
     */
    @JMap
    private Train train;

    @JMap
    private Train.DRIVING_DIRECTION trainDrivingDirection;
    /**
     * Route to drive from start to end station.
     * TODO: interstations aren't supported yet
     */

    @JMap
    private List<RouteSequence> routeSequences;

    private RUN_STATE runState = RUN_STATE.IDLE;

    private MODE mode = MODE.OFF;

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

    public Train getTrain() {
        return train;
    }

    public void setTrain(Train train) {
        this.train = train;
    }

    public List<RouteSequence> getRouteSequences() {
        return routeSequences;
    }

    public void setRouteSequences(List<RouteSequence> routeSequences) {
        this.routeSequences = routeSequences;
    }

    public RUN_STATE getRunState() {
        return runState;
    }

    public void setRunState(RUN_STATE runState) {
        this.runState = runState;
    }

    public MODE getMode() {
        return mode;
    }

    public void setMode(MODE mode) {
        this.mode = mode;
    }

    public Train.DRIVING_DIRECTION getTrainDrivingDirection() {
        return trainDrivingDirection;
    }

    public void setTrainDrivingDirection(Train.DRIVING_DIRECTION trainDrivingDirection) {
        this.trainDrivingDirection = trainDrivingDirection;
    }

    /**
     * TODO obsolete by getFirstRouteBlock ?
     * 
     * @param signal
     * @return
     */
    public Optional<RouteBlock> getRouteBlockForStartSignal(Signal signal) {
        for (RouteSequence routeSequence : routeSequences) {
            if (routeSequence.getRoute() != null) {
                for (RouteBlock routeBlock : routeSequence.getRoute().getRouteBlocks()) {
                    if (routeBlock.getStartPoint().equals(signal)) {
                        return Optional.of(routeBlock);
                    }
                }
            }
        }
        return Optional.absent();
    }

    /**
     * Get the first {@link RouteBlock} of the scenario.
     *
     * @return {@link Optional} of {@link RouteBlock}
     */
    public Optional<RouteBlock> getFirstRouteBlock() {
        if (!routeSequences.isEmpty()) {
            Route route = routeSequences.get(0).getRoute();
            if (route != null) {
                return route.getFirstRouteBlock();
            }
        }
        return Optional.absent();
    }

    /**
     * Return the {@link TrackBlock} which is the endpoint of the scenario.
     *
     * @return {@link TrackBlock} or {@code null}
     */
    public TrackBlock getEndPoint() {
        if (!routeSequences.isEmpty()) {
            Route route = routeSequences.get(routeSequences.size() - 1).getRoute();
            if (route != null) {
                Optional<RouteBlock> lastRouteBlock = route.getLastRouteBlock();
                if (lastRouteBlock.isPresent()) {
                    TrackBlock endPoint = lastRouteBlock.get().getEndPoint();
                    if (endPoint != null) {
                        return endPoint;
                    }
                }
            }
        }
        throw new RuntimeException("scenario has no valid endpoint");
    }

    /**
     * State of the actual execution.
     */
    public enum RUN_STATE {
        /**
         * Scenario is currently running.
         */
        RUNNING,
        /**
         * TODO
         */
        IDLE,
        /**
         * TODO
         */
        PAUSED,
        /**
         * Scenario is stopped.
         */
        STOPPED
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

}
