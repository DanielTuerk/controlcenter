package net.wbz.moba.controlcenter.web.server.web.scenario;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import java.util.ArrayList;
import java.util.List;
import net.wbz.moba.controlcenter.web.server.event.EventBroadcaster;
import net.wbz.moba.controlcenter.web.server.persist.scenario.StationDao;
import net.wbz.moba.controlcenter.web.server.persist.scenario.StationDataMapper;
import net.wbz.moba.controlcenter.web.shared.scenario.Station;
import net.wbz.moba.controlcenter.web.shared.scenario.StationDataChangedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manager to access the {@link Station}s from database. The data is cached.
 *
 * @author Daniel Tuerk
 */
@Singleton
public class StationManager {

    private static final Logger LOG = LoggerFactory.getLogger(StationManager.class);

    /**
     * Cached scenarios from persistence.
     */
    private final List<Station> stations = new ArrayList<>();

    private final StationDao stationDao;
    private final EventBroadcaster eventBroadcaster;
    private final StationDataMapper stationDataMapper;

    @Inject
    public StationManager(StationDao stationDao, EventBroadcaster eventBroadcaster,
        StationDataMapper stationDataMapper) {
        this.stationDao = stationDao;
        this.eventBroadcaster = eventBroadcaster;
        this.stationDataMapper = stationDataMapper;
    }

    synchronized List<Station> getStations() {
        if (stations.isEmpty()) {
            loadStationsFromDatabase();
        }
        return stations;
    }


    @Transactional
    void createStation(Station station) {
        stationDao.create(stationDataMapper.transformTarget(station));
    }

    @Transactional
    void updateStation(Station station) {
        stationDao.update(stationDataMapper.transformTarget(station));
    }

    @Transactional
    void deleteStation(long stationId) {
        stationDao.delete(stationDao.findById(stationId));
    }

    private void loadStationsFromDatabase() {
        LOG.debug("load stations from database");
        stations.clear();
        stations.addAll(stationDataMapper.transformSource(stationDao.listAll()));
    }


    private void fireStationsChanged() {
        eventBroadcaster.fireEvent(new StationDataChangedEvent());
    }

}
