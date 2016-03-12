package net.wbz.moba.controlcenter.web.server.constrution;

import java.util.List;

import javax.inject.Provider;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import net.wbz.moba.controlcenter.web.shared.constrution.Construction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

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

//    @Override
    public Construction getCurrentConstruction() {
        return currentConstruction;
    }
//
//    @Override
    public void setCurrentConstruction(Construction construction) {
        currentConstruction = construction;
    }
//
//    @Override
    @Transactional
    public Construction createConstruction(Construction construction) {
        entityManager.get().persist(construction);
        return construction;
    }
//
//    @Override
    public synchronized List<Construction> loadConstructions() {
        log.info("load construction");
        Query typedQuery = entityManager.get().createQuery(
                "SELECT x FROM Construction x");
        List<Construction> resultList = typedQuery.getResultList();
        return resultList;
    }

    public void persist() {
//        EntityManager em = entityManager.get().persist();
//        try {
//            em.persist(this);
//        } finally {
//            em.close();
//        }
    }

    public void remove() {
//        EntityManager em = entityManager();
//        try {
//            Employee attached = em.find(Employee.class, this.id);
//            em.remove(attached);
//        } finally {
//            em.close();
//        }
    }
}
