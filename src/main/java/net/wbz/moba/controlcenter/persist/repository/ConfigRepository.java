package net.wbz.moba.controlcenter.persist.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import net.wbz.moba.controlcenter.persist.entity.ConfigValueEntity;

/**
 * @author Daniel Tuerk
 */
@ApplicationScoped
public class ConfigRepository implements PanacheRepositoryBase<ConfigValueEntity, String> {

    public List<ConfigValueEntity> listAll() {
        return listAll(Sort.by("key"));
    }

}
