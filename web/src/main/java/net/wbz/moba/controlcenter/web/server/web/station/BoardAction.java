package net.wbz.moba.controlcenter.web.server.web.station;

import java.util.function.Consumer;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario;
import net.wbz.moba.controlcenter.web.shared.station.Station;

/**
 * @author Daniel Tuerk
 */
class BoardAction {

    private final StationManager stationManager;
    private final Scenario scenario;
    private final Long stationPlatformStart;
    private final Long stationPlatformEnd;
    private Station stationStart;
    private Station stationEnd;

    BoardAction(Scenario scenario, StationManager stationManager) {
        this.scenario = scenario;
        this.stationPlatformStart = scenario.getStationPlatformStartId();
        this.stationPlatformEnd = scenario.getStationPlatformEndId();
        this.stationManager = stationManager;
    }

    void apply(Consumer<BoardAction> consumer) {
        if (stationPlatformStart != null && stationPlatformEnd != null) {
            stationStart = stationManager.getStationOfPlatform(stationPlatformStart);
            stationEnd = stationManager.getStationOfPlatform(stationPlatformEnd);
            consumer.accept(this);
        }
    }

    public Long getStationPlatformStart() {
        return stationPlatformStart;
    }

    public Long getStationPlatformEnd() {
        return stationPlatformEnd;
    }

    public Station getStationStart() {
        return stationStart;
    }

    public Station getStationEnd() {
        return stationEnd;
    }

    public Scenario getScenario() {
        return scenario;
    }
}
