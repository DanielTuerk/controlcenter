package net.wbz.moba.controlcenter.persist.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import net.wbz.moba.controlcenter.persist.entity.RouteEntity;

/**
 * @author Daniel Tuerk
 */
@ApplicationScoped
public class RouteRepository implements PanacheRepository<RouteEntity> {

    public List<RouteEntity> listAll() {
        // TODO
        return getEntityManager().createQuery("SELECT x FROM SCENARIO_ROUTE x ORDER BY x.name", RouteEntity.class)
                .getResultList();
    }

}
