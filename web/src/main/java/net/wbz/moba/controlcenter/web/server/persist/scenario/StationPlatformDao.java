package net.wbz.moba.controlcenter.web.server.persist.scenario;



import java.util.List;
import javax.inject.Provider;
import jakarta.persistence.EntityManager;
import net.wbz.moba.controlcenter.web.server.persist.AbstractDao;
import org.jboss.logging.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Daniel Tuerk
 */
@Singleton
public class StationPlatformDao implements PanacheRepository<StationPlatformEntity> {

    private static final Logger LOG = Logger.getLogger(StationPlatformDao.class);

    @Inject
    public StationPlatformDao(Provider<EntityManager> entityManager) {
        super(entityManager, StationPlatformEntity.class);
    }

    public List<StationPlatformEntity> listAll() {
        return getEntityManager().createQuery("SELECT x FROM STATION_PLATFORM x ORDER BY x.name",
            StationPlatformEntity.class)
            .getResultList();
    }

    public List<StationPlatformEntity> findByStation(Long stationId) {
        return getEntityManager().createQuery(
            "SELECT x FROM STATION_PLATFORM x where x.station.id = :stationId ORDER BY x.name",
            StationPlatformEntity.class)
            .setParameter("stationId", stationId)
            .getResultList();
    }

    public void deleteByStation(Long stationId) {
        getEntityManager().createQuery(
            "DELETE FROM STATION_PLATFORM x where x.station.id = :stationId")
            .setParameter("stationId", stationId)
            .executeUpdate();
    }


}
