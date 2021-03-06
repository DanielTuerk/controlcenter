package net.wbz.moba.controlcenter.web.server.persist.construction.track;

import javax.inject.Provider;
import javax.persistence.EntityManager;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import net.wbz.moba.controlcenter.web.server.persist.AbstractDao;

/**
 * @author Daniel Tuerk
 */
@Singleton
public class BusDataConfigurationDao extends AbstractDao<BusDataConfigurationEntity> {
    @Inject
    public BusDataConfigurationDao(Provider<EntityManager> entityManager) {
        super(entityManager, BusDataConfigurationEntity.class);
    }
}
