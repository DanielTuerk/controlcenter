package net.wbz.moba.controlcenter.web.server.persist.construction.track;

import javax.inject.Provider;
import jakarta.persistence.EntityManager;




import net.wbz.moba.controlcenter.web.server.persist.AbstractDao;

/**
 * @author Daniel Tuerk
 */
@Singleton
public class GridPositionDao implements PanacheRepository<GridPositionEntity> {
    @Inject
    public GridPositionDao(Provider<EntityManager> entityManager) {
        super(entityManager, GridPositionEntity.class);
    }

    public GridPositionEntity findByGridPositionCopy(GridPositionEntity input) {
        return getEntityManager().createQuery("SELECT g FROM GRID_POSITION g"
                + " WHERE g.x = :x AND g.y = :y", GridPositionEntity.class)
                .setParameter("x", input.getX())
                .setParameter("y", input.getY())
                .getSingleResult();
    }
}
