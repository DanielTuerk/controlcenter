package io.github.danieltuerk.controlcenter.persist.repository;

import io.github.danieltuerk.controlcenter.persist.entity.ScenarioEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

/**
 * @author Daniel Tuerk
 */
@ApplicationScoped
public class ScenarioRepository implements PanacheRepository<ScenarioEntity> {

    public List<ScenarioEntity> listAll(Long constructionId) {
        return list("construction.id=?1", Sort.by("name"), constructionId);
    }

}
