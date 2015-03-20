package net.wbz.moba.controlcenter.web.server.constrution;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import net.sf.gilead.core.PersistentBeanManager;
import net.sf.gilead.gwt.PersistentRemoteService;
import net.wbz.moba.controlcenter.web.shared.constrution.Construction;
import net.wbz.moba.controlcenter.web.shared.constrution.ConstructionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Provider;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
@Singleton
public class ConstructionServiceImpl extends PersistentRemoteService implements ConstructionService {

    private static final Logger log = LoggerFactory.getLogger(ConstructionServiceImpl.class);

    private final Provider<EntityManager> entityManager;

    private Construction currentConstruction = null;

    @Inject
    public ConstructionServiceImpl(Provider<EntityManager> entityManager, PersistentBeanManager persistentBeanManager) {
        this.entityManager = entityManager;
        setBeanManager(persistentBeanManager);
    }

    @Override
    public Construction getCurrentConstruction() {
        return currentConstruction;
    }

    @Override
    @Transactional
    public Construction createConstruction(Construction construction) {
        entityManager.get().persist(construction);
        return construction;
    }

    @Override
    public synchronized Construction[] loadConstructions() {
        log.info("load construction");
        Query typedQuery = entityManager.get().createQuery(
                "SELECT x FROM Construction x");
        List<Construction> resultList = typedQuery.getResultList();
        return resultList.toArray(new Construction[resultList.size()]);
    }

    @Override
    public void setCurrentConstruction(Construction construction) {
        currentConstruction = construction;
    }
}
