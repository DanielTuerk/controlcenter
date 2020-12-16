package net.wbz.moba.controlcenter.web.server.web.station;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import java.util.Collection;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.wbz.moba.controlcenter.web.shared.station.Station;
import net.wbz.moba.controlcenter.web.shared.station.StationEditorService;

/**
 * @author Daniel Tuerk
 */
@Singleton
public class StationEditorServiceImpl extends RemoteServiceServlet implements StationEditorService {

    private final StationManager stationManager;

    @Inject
    public StationEditorServiceImpl(StationManager stationManager) {
        this.stationManager = stationManager;
    }

    @Override
    public Collection<Station> getStations() {
        return stationManager.getStations();
    }

    @Override
    public void createStation(Station station) {
        stationManager.createStation(station);
    }

    @Override
    public void updateStation(Station station) {
        stationManager.updateStation(station);
    }

    @Override
    public void deleteStation(long stationId) {
        stationManager.deleteStation(stationId);
    }

}
