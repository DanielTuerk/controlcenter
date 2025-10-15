package net.wbz.moba.controlcenter.persist.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import net.wbz.moba.controlcenter.persist.entity.ScenarioEntity;

/**
 * @author Daniel Tuerk
 */
@ApplicationScoped
public class ScenarioRepository implements PanacheRepository<ScenarioEntity> {

    public List<ScenarioEntity> listAll() {
        return listAll(Sort.by("name"));
    }

}
