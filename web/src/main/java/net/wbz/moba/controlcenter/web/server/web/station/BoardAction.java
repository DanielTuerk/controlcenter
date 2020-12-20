package net.wbz.moba.controlcenter.web.server.web.station;

import java.util.Set;
import java.util.function.Consumer;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario;
import net.wbz.moba.controlcenter.web.shared.station.Station;
import net.wbz.moba.controlcenter.web.shared.station.StationPlatform;

/**
 * @author Daniel Tuerk
 */
class BoardAction {

    private final Scenario scenario;
    private final StationPlatform stationPlatformStart;
    private final StationPlatform stationPlatformEnd;
    private final Station stationStart;
    private final Station stationEnd;
    private final Set<Station> viaStations;

    public BoardAction(Scenario scenario, Station stationStart, StationPlatform stationPlatformStart,
        Station stationEnd,
        StationPlatform stationPlatformEnd, Set<Station> viaStations) {
        this.scenario = scenario;
        this.stationPlatformStart = stationPlatformStart;
        this.stationPlatformEnd = stationPlatformEnd;
        this.stationStart = stationStart;
        this.stationEnd = stationEnd;
        this.viaStations = viaStations;
    }

    void apply(Consumer<BoardAction> consumer) {
        if (stationPlatformStart != null && stationPlatformEnd != null) {
            consumer.accept(this);
        }
    }

    public StationPlatform getStationPlatformStart() {
        return stationPlatformStart;
    }

    public StationPlatform getStationPlatformEnd() {
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

    public Set<Station> getViaStations() {
        return viaStations;
    }
}
