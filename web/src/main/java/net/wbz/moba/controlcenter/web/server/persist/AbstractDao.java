package net.wbz.moba.controlcenter.web.server.persist;

import java.util.List;
import javax.inject.Provider;
import javax.persistence.EntityManager;

import org.hibernate.Session;

import net.wbz.moba.controlcenter.web.shared.Identity;

/**
 * Abstract DAO for CRUD operations to the defined {@link Identity}.
 *
 * @author Daniel Tuerk
 */
public abstract class AbstractDao<T extends Identity> {
    private final Provider<EntityManager> entityManager;
    private Class<T> entityClazz;

    /**
     * Create DAO for the given entity class.
     *
     * @param entityManager {@link Provider<EntityManager>}
     * @param entityClazz class of entity
     */
    public AbstractDao(Provider<EntityManager> entityManager, Class<T> entityClazz) {
        this.entityManager = entityManager;
        this.entityClazz = entityClazz;
    }

    public T create(T entity) {
        getEntityManager().persist(entity);
        getEntityManager().merge(entity);
        return entity;
    }

    public void update(T entity) {
        getEntityManager().merge(entity);
    }

    public void delete(T entity) {
        T merge = getEntityManager().merge(entity);
        getEntityManager().remove(merge);
    }

    /**
     * Find entity for the given id.
     *
     * @param id id of entity
     * @return {@link T} of id or {@code null} if not existing
     */
    public T findById(Long id) {
        return getEntityManager().find(entityClazz, id);
    }

    protected EntityManager getEntityManager() {
        return entityManager.get();
    }

    protected Session getSession() {
        return getEntityManager().unwrap(Session.class);
    }

    public void flush() {
        getEntityManager().flush();
    }
}
