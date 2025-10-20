package net.wbz.moba.controlcenter.web.server.persist.construction.track;

import javax.inject.Provider;
import jakarta.persistence.EntityManager;




import net.wbz.moba.controlcenter.web.server.persist.AbstractDao;

/**
 * @author Daniel Tuerk
 */
@Singleton
public class BusDataConfigurationDao implements PanacheRepository<BusDataConfigurationEntity> {
    @Inject
    public BusDataConfigurationDao(Provider<EntityManager> entityManager) {
        super(entityManager, BusDataConfigurationEntity.class);
    }
}
