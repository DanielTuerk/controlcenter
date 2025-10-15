package net.wbz.moba.controlcenter.web.server.persist.construction;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import java.util.List;
import javax.inject.Provider;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import net.wbz.moba.controlcenter.web.server.persist.AbstractDao;

/**
 * @author Daniel Tuerk
 */
@Singleton
@Slf4j
public class ConstructionDao implements PanacheRepository<ConstructionEntity> {

    @Inject
    public ConstructionDao(Provider<EntityManager> entityManager) {
        super(entityManager, ConstructionEntity.class);
    }

    @Transactional
    public synchronized List<ConstructionEntity> listConstructions() {
        log.debug("load constructions");
        return getEntityManager().createQuery("SELECT x FROM CONSTRUCTION x", ConstructionEntity.class).getResultList();
    }

}
