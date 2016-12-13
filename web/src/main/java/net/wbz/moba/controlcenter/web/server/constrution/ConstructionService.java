package net.wbz.moba.controlcenter.web.server.constrution;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import net.wbz.moba.controlcenter.web.shared.constrution.Construction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Provider;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

/**
 * @author Daniel Tuerk
 */
@Singleton
public class ConstructionService {

    private static final Logger log = LoggerFactory.getLogger(ConstructionService.class);

    private final Provider<EntityManager> entityManager;

    private Construction currentConstruction = null;

    @Inject
    public ConstructionService(Provider<EntityManager> entityManager) {
        this.entityManager = entityManager;
    }

    public Construction getCurrentConstruction() {
        return currentConstruction;
    }

    public void setCurrentConstruction(Construction construction) {
        currentConstruction = construction;
    }

    @Transactional
    public void createConstruction(Construction construction) {
        entityManager.get().persist(construction);
    }

    @Transactional
    public synchronized List<Construction> loadConstructions() {
        log.info("load construction");
        Query typedQuery = entityManager.get().createQuery(
                "SELECT x FROM Construction x");
        List<Construction> resultList = typedQuery.getResultList();
        return resultList;
    }

}
