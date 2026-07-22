package io.github.danieltuerk.controlcenter.persist.repository;

import io.github.danieltuerk.controlcenter.persist.entity.ConfigValueEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

/**
 * @author Daniel Tuerk
 */
@ApplicationScoped
public class ConfigRepository implements PanacheRepositoryBase<ConfigValueEntity, String> {

    public List<ConfigValueEntity> listAll() {
        return listAll(Sort.by("key"));
    }

}
