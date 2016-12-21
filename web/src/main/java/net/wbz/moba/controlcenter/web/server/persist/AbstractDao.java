package net.wbz.moba.controlcenter.web.server.persist;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import net.wbz.moba.controlcenter.web.shared.Identity;

import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

/**
 * Abstract DAO for CRUD operations to the defined {@link Identity}.
 *
 * @author Daniel Tuerk
 */
public abstract class AbstractDao<T extends Identity> {
    private final Provider<EntityManager> entityManager;

    @Inject
    public AbstractDao(Provider<EntityManager> entityManager) {
        this.entityManager = entityManager;
    }

    public abstract T getById(Long id);

    @Transactional
    public void create(T entity) {
        entityManager.get().merge(entity);
    }

    @Transactional
    public void update(T entity) {
        entityManager.get().persist(entity);
    }

    @Transactional
    public void delete(T entity) {
        entityManager.get().remove(entity);
    }

    @SuppressWarnings("unchecked")
    protected  List<T> safeList(Query typedQuery) {
        return typedQuery.getResultList();
    }

    protected EntityManager getEntityManager() {
        return entityManager.get();
    }
}
