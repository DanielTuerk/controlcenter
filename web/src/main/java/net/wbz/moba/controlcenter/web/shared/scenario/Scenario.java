package net.wbz.moba.controlcenter.web.shared.scenario;

import java.util.List;

import com.google.common.base.Optional;
import com.googlecode.jmapper.annotations.JMap;

import net.wbz.moba.controlcenter.web.shared.track.model.AbstractDto;
import net.wbz.moba.controlcenter.web.shared.track.model.Signal;
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

    /**
     * Route to drive from start to end station.
     * TODO: interstations aren't supported yet
     */
    @JMap
    private List<Route> routes;

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

    public List<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
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

    public Optional<RouteBlock> getRouteBlockForStartSignal(Signal signal) {
        for (Route route : routes) {
            for (RouteBlock routeBlock : route.getRouteBlocks()) {
                if (routeBlock.getStartPoint().equals(signal)) {
                    return Optional.of(routeBlock);
                }
            }

        }
        return Optional.absent();
    }

    public Optional<RouteBlock> getFirstRouteBlock() {
        if (routes.isEmpty()) {
            return Optional.absent();
        }
        return routes.get(0).getFirstRouteBlock();
    }

    public enum RUN_STATE {
        RUNNING, IDLE, PAUSED
    }

    public enum MODE {
        OFF, MANUAL, AUTOMATIC
    }

}
