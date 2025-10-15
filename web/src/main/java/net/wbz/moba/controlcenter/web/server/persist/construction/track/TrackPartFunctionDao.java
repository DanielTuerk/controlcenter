package net.wbz.moba.controlcenter.web.server.persist.construction.track;

import javax.inject.Provider;
import jakarta.persistence.EntityManager;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import net.wbz.moba.controlcenter.web.server.persist.AbstractDao;

/**
 * @author Daniel Tuerk
 */
@Singleton
public class TrackPartFunctionDao implements PanacheRepository<TrackPartFunctionEntity> {
    @Inject
    public TrackPartFunctionDao(Provider<EntityManager> entityManager) {
        super(entityManager, TrackPartFunctionEntity.class);
    }
}
