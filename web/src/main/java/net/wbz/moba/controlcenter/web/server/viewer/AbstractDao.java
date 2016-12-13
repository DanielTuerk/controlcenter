package net.wbz.moba.controlcenter.web.server.viewer;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import net.wbz.moba.controlcenter.web.shared.Identity;

import javax.inject.Provider;
import javax.persistence.EntityManager;

/**
 * Abstract DAO for CRUD operations to the defined {@link Identity}.
 *
 * @author Daniel Tuerk
 */
abstract class AbstractDao<T extends Identity> {
    private final Provider<EntityManager> entityManager;

    @Inject
    public AbstractDao(Provider<EntityManager> entityManager) {
        this.entityManager = entityManager;
    }

    protected EntityManager getEntityManager() {
        return entityManager.get();
    }

    @Transactional
    public void create(T trackPart) {
        entityManager.get().merge(trackPart);
    }

    @Transactional
    public void update(T trackPart) {
        entityManager.get().persist(trackPart);
    }

    @Transactional
    public void delete(T trackPart) {
        entityManager.get().remove(trackPart);
    }
}
