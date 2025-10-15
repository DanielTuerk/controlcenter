package net.wbz.moba.controlcenter.persist.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import net.wbz.moba.controlcenter.persist.entity.StationEntity;

/**
 * @author Daniel Tuerk
 */
@ApplicationScoped
public class StationRepository implements PanacheRepository<StationEntity> {

    public List<StationEntity> listAll() {
        return listAll(Sort.by("name"));
    }

    public StationEntity findByPlatformId(long platformId) {
        // TODO
        return getEntityManager()
            .createQuery("SELECT s FROM STATION_PLATFORM p JOIN p.station s WHERE p.id = :platformId", StationEntity.class)
            .setParameter("platformId", platformId)
            .getSingleResult();
    }

}
