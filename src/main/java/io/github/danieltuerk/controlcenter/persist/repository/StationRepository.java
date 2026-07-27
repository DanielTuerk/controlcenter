package io.github.danieltuerk.controlcenter.persist.repository;

import io.github.danieltuerk.controlcenter.persist.entity.StationEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

/**
 * @author Daniel Tuerk
 */
@ApplicationScoped
public class StationRepository implements PanacheRepository<StationEntity> {

    public List<StationEntity> listAll() {
        return listAll(Sort.by("name"));
    }

}
