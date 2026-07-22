package io.github.danieltuerk.controlcenter.persist.repository.track;

import io.github.danieltuerk.controlcenter.persist.entity.track.GridPositionEntity;
import io.github.danieltuerk.controlcenter.shared.track.model.GridPosition;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * @author Daniel Tuerk
 */
@ApplicationScoped
public class GridPositionRepository implements PanacheRepository<GridPositionEntity> {

    public GridPositionEntity findByGridPosition(GridPosition input) {
        return find("x=?1 AND y=?2", input.getX(), input.getY()).firstResultOptional().orElse(null);
    }
}
