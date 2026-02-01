package net.wbz.moba.controlcenter.persist.repository.track;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import net.wbz.moba.controlcenter.persist.entity.track.GridPositionEntity;
import net.wbz.moba.controlcenter.shared.track.model.GridPosition;

/**
 * @author Daniel Tuerk
 */
@ApplicationScoped
public class GridPositionRepository implements PanacheRepository<GridPositionEntity> {

    public GridPositionEntity findByGridPosition(GridPosition input) {
        return getEntityManager().createQuery("SELECT g FROM GRID_POSITION g"
                + " WHERE g.x = :x AND g.y = :y", GridPositionEntity.class)
                .setParameter("x", input.getX())
                .setParameter("y", input.getY())
                .getSingleResult();
    }
}
