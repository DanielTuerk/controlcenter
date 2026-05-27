package net.wbz.moba.controlcenter.persist.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import net.wbz.moba.controlcenter.persist.entity.RouteEntity;

import java.util.List;

/**
 * @author Daniel Tuerk
 */
@ApplicationScoped
public class RouteRepository implements PanacheRepository<RouteEntity> {

    public List<RouteEntity> listAll(Long constructionId) {
        return list("construction.id=?1", Sort.by("name"), constructionId);
    }

}
