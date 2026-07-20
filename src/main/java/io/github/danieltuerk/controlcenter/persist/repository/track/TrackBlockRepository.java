package io.github.danieltuerk.controlcenter.persist.repository.track;

import io.github.danieltuerk.controlcenter.persist.entity.track.TrackBlockEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

/**
 * @author Daniel Tuerk
 */
@ApplicationScoped
public class TrackBlockRepository implements PanacheRepository<TrackBlockEntity> {

    public List<TrackBlockEntity> findByConstructionId(Long constructionId) {
        return list("constructionId=?1", Sort.by("name"), constructionId);
    }

}
