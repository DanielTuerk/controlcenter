package net.wbz.moba.controlcenter.web.shared.scenario;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.googlecode.jmapper.annotations.JMap;
import java.util.ArrayList;
import java.util.List;
import net.wbz.moba.controlcenter.web.shared.track.model.AbstractDto;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackBlock;
import net.wbz.moba.controlcenter.web.shared.train.Train;

/**
 * @author Daniel Tuerk
 */
public class  Scenario extends AbstractDto {

    /**
     * State of the actual execution.
     */
    public enum RUN_STATE implements IsSerializable {
        /**
         * Scenario is currently running.
         */
        RUNNING,
        /**
         * Scheduled execution waiting for cron.
         */
        IDLE,
        /**
         * TODO
         */
        PAUSED,
        /**
         * Scenario is stopped.
         */
        STOPPED,
        /**
         * Scenario is finished.
         */
        FINISHED
    }
    /**
     * Mode of the scenario execution.
     */
    public enum MODE implements IsSerializable {
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
    @JMap
    private String name;
    @JMap
    private String cron;
    /**
     * TODO also looking for position on track - drive to possible start point, or resume on actual pos TODO check
     * existing on track
     */
    @JMap
    private Train train;
    @JMap
    private Train.DRIVING_DIRECTION trainDrivingDirection;
    @JMap
    private Integer startDrivingLevel;
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
        if (routeSequences == null) {
            routeSequences = new ArrayList<>();
        }
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

    public Integer getStartDrivingLevel() {
        return startDrivingLevel;
    }

    public void setStartDrivingLevel(Integer startDrivingLevel) {
        this.startDrivingLevel = startDrivingLevel;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Scenario{");
        sb.append("name='").append(name).append('\'');
        sb.append(", cron='").append(cron).append('\'');
        sb.append(", train=").append(train);
        sb.append(", trainDrivingDirection=").append(trainDrivingDirection);
        sb.append(", startDrivingLevel=").append(startDrivingLevel);
        sb.append(", routeSequences=").append(routeSequences);
        sb.append(", runState=").append(runState);
        sb.append(", mode=").append(mode);
        sb.append('}');
        return sb.toString();
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
                return route.getEnd();
            }
        }
        throw new RuntimeException("scenario has no valid endpoint");
    }

}
