package net.wbz.moba.controlcenter.web.server.web.station;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import java.util.ArrayList;
import java.util.List;
import net.wbz.moba.controlcenter.web.server.event.EventBroadcaster;
import net.wbz.moba.controlcenter.web.server.persist.scenario.StationDao;
import net.wbz.moba.controlcenter.web.server.persist.scenario.StationDataMapper;
import net.wbz.moba.controlcenter.web.server.persist.scenario.StationEntity;
import net.wbz.moba.controlcenter.web.server.persist.scenario.StationPlatformDao;
import net.wbz.moba.controlcenter.web.server.persist.scenario.StationPlatformDataMapper;
import net.wbz.moba.controlcenter.web.server.persist.scenario.StationPlatformEntity;
import net.wbz.moba.controlcenter.web.shared.station.Station;
import net.wbz.moba.controlcenter.web.shared.station.StationDataChangedEvent;
import net.wbz.moba.controlcenter.web.shared.station.StationPlatform;
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
     * Cached {@link Station}s from persistence.
     */
    private final List<Station> stations = new ArrayList<>();
    /**
     * Cached {@link StationPlatform}s from persistence.
     */
    private final List<StationPlatform> stationPlatforms = new ArrayList<>();

    private final StationDao stationDao;
    private final EventBroadcaster eventBroadcaster;
    private final StationDataMapper stationDataMapper;
    private final StationPlatformDataMapper stationPlatformDataMapper;
    private final StationPlatformDao stationPlatformDao;

    @Inject
    public StationManager(StationDao stationDao, EventBroadcaster eventBroadcaster,
        StationDataMapper stationDataMapper,
        StationPlatformDataMapper stationPlatformDataMapper,
        StationPlatformDao stationPlatformDao) {
        this.stationDao = stationDao;
        this.eventBroadcaster = eventBroadcaster;
        this.stationDataMapper = stationDataMapper;
        this.stationPlatformDataMapper = stationPlatformDataMapper;
        this.stationPlatformDao = stationPlatformDao;
    }

    synchronized List<Station> getStations() {
        if (stations.isEmpty()) {
            loadStationsFromDatabase();
        }
        return stations;
    }

    synchronized List<StationPlatform> getStationPlatforms() {
        if (stationPlatforms.isEmpty()) {
            loadStationPlatformsFromDatabase();
        }
        return stationPlatforms;
    }

    @Transactional
    void createStation(Station station) {
        StationEntity entity = stationDataMapper.transformTarget(station);
        entity.getPlatforms().clear();
        StationEntity createdStationEntity = stationDao.create(entity);
        createOrUpdatePlatforms(station.getPlatforms(), createdStationEntity);

        stationDao.update(createdStationEntity);

        loadStationsFromDatabase();
        loadStationPlatformsFromDatabase();
        fireStationsChanged();
    }

    @Transactional
    void updateStation(Station station) {
        StationEntity entity = stationDataMapper.transformTarget(station);

        createOrUpdatePlatforms(station.getPlatforms(), entity);

        stationDao.update(entity);
        loadStationsFromDatabase();
        loadStationPlatformsFromDatabase();
        fireStationsChanged();
    }

    @Transactional
    void deleteStation(long stationId) {
        stationPlatformDao.deleteByStation(stationId);
        stationDao.delete(stationDao.findById(stationId));
        loadStationsFromDatabase();
        loadStationPlatformsFromDatabase();
        fireStationsChanged();
    }

    private void createOrUpdatePlatforms(List<StationPlatform> stationPlatforms, StationEntity stationEntity) {
        // create or update route sequences
        List<StationPlatformEntity> entities = new ArrayList<>();
        for (StationPlatform routeBlockPart : stationPlatforms) {
            StationPlatformEntity routeEntity = stationPlatformDataMapper.transformTarget(routeBlockPart);
            routeEntity.setStation(stationEntity);
            if (routeBlockPart.getId() == null) {
                routeEntity = stationPlatformDao.create(routeEntity);
            } else {
                stationPlatformDao.update(routeEntity);
            }
            entities.add(routeEntity);
        }
        // delete removed route sequences
        List<StationPlatformEntity> stationPlatformEntities = new ArrayList<>(
            stationPlatformDao.findByStation(stationEntity.getId()));
        stationPlatformEntities.removeAll(entities);
        for (StationPlatformEntity routeSequenceEntity : stationPlatformEntities) {
            stationPlatformDao.delete(routeSequenceEntity);
        }

        // set to actual merged entities
        stationEntity.setPlatforms(entities);
    }

    private void loadStationsFromDatabase() {
        LOG.debug("load stations from database");
        stations.clear();
        stations.addAll(stationDataMapper.transformSource(stationDao.listAll()));
    }

    private void loadStationPlatformsFromDatabase() {
        LOG.debug("load station platforms from database");
        stationPlatforms.clear();
        stationPlatforms.addAll(stationPlatformDataMapper.transformSource(stationPlatformDao.listAll()));
    }

    private void fireStationsChanged() {
        eventBroadcaster.fireEvent(new StationDataChangedEvent());
    }

    public Station getStationOfPlatform(Long stationPlatformId) {
        // TODO cache oder nicht, oder veilleicht doch einfach @Cacheable ?
        return stationDataMapper.transformSource(stationDao.findByPlatformId(stationPlatformId));
    }

    public StationPlatform findStationPlatform(long stationPlatformId) {
        // TODO chached nicht mehr, lieber stream?
        return stationPlatformDataMapper.transformSource(stationPlatformDao.findById(stationPlatformId));
    }
}
