package net.wbz.moba.controlcenter.web.guice.requestFactory;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.web.bindery.requestfactory.shared.Locator;
import net.wbz.moba.controlcenter.web.shared.HasVersionAndId;

import javax.persistence.EntityManager;

/**
 * @author Daniel Tuerk
 */
public class InjectingEntityLocator<T extends HasVersionAndId> extends Locator<T, Long> {
    @Inject
    Provider<EntityManager> data;

    @Inject
    Injector injector;

    @Override
    public T create(Class<? extends T> clazz) {
        return injector.getInstance(clazz);
    }

    @Override
    public T find(Class<? extends T> clazz, Long id) {
        return data.get().find(clazz, id);
    }

    @Override
    public Class<T> getDomainType() {
        throw new UnsupportedOperationException();//unused
    }

    @Override
    public Long getId(T domainObject) {
        return domainObject.getId();
    }

    @Override
    public Class<Long> getIdType() {
        return Long.class;
    }

    @Override
    public Object getVersion(T domainObject) {
        return domainObject.getVersion();
    }
}
