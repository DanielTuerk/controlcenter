package net.wbz.moba.controlcenter.web.server.web.station;

import java.util.Collection;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.wbz.moba.controlcenter.web.shared.station.Station;

/**
 * @author Daniel Tuerk
 */
@Singleton
public class StationEditorService {

    private final StationManager stationManager;

    @Inject
    public StationEditorService(StationManager stationManager) {
        this.stationManager = stationManager;
    }

    public Collection<Station> getStations() {
        return stationManager.getStations();
    }

    public void createStation(Station station) {
        stationManager.createStation(station);
    }

    public void updateStation(Station station) {
        stationManager.updateStation(station);
    }

    public void deleteStation(long stationId) {
        stationManager.deleteStation(stationId);
    }

}
