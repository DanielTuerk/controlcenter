package net.wbz.moba.controlcenter.web.server.constrution;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import net.wbz.moba.controlcenter.web.shared.constrution.Construction;
import net.wbz.moba.controlcenter.web.shared.constrution.ConstructionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Provider;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
@Singleton
public class ConstructionServiceImpl extends RemoteServiceServlet implements ConstructionService {

    private static final Logger log = LoggerFactory.getLogger(ConstructionServiceImpl.class);

    private final Provider<EntityManager> entityManager;

    private Construction currentConstruction = null;

    @Inject
    public ConstructionServiceImpl(Provider<EntityManager> entityManager) {
        this.entityManager = entityManager;
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
        TypedQuery<Construction> typedQuery = entityManager.get().createQuery(
                "SELECT x FROM Construction x", Construction.class);
        List<Construction> resultList = typedQuery.getResultList();
        return resultList.toArray(new Construction[resultList.size()]);
    }

    @Override
    public void setCurrentConstruction(Construction construction) {
        currentConstruction = construction;
    }
}
