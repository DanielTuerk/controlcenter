package net.wbz.moba.controlcenter.web.server.persist.construction.track;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.wbz.moba.controlcenter.web.server.persist.AbstractDao;

import javax.inject.Provider;
import javax.persistence.EntityManager;

/**
 * @author Daniel Tuerk
 */
@Singleton
public class GridPositionDao extends AbstractDao<GridPositionEntity> {
    @Inject
    public GridPositionDao(Provider<EntityManager> entityManager) {
        super(entityManager, GridPositionEntity.class);
    }
}
