package net.wbz.moba.controlcenter.web.server.web.config;

import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import net.wbz.moba.controlcenter.web.server.persist.config.ConfigValueEntity;
import net.wbz.moba.controlcenter.web.shared.config.ConfigNotAvailableException;

import net.wbz.moba.controlcenter.web.shared.config.ConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;

/**
 * TODO dao
 * TODO move to web package
 * @author Daniel Tuerk
 */
@Singleton
public class ConfigServiceImpl  extends RemoteServiceServlet implements ConfigService {
    private static final Logger log = LoggerFactory.getLogger(ConfigServiceImpl.class);

    private final Provider<EntityManager> entityManager;

    @Inject
    public ConfigServiceImpl(Provider<EntityManager> entityManager) {
        this.entityManager = entityManager;
    }

    @Transactional
    public String loadValue(String configKey) throws ConfigNotAvailableException {
        Query typedQuery = entityManager.get().createQuery(
                "SELECT x FROM ConfigValueEntity x where key=" + configKey);
        ConfigValueEntity resultList = (ConfigValueEntity) typedQuery.getSingleResult();
        if (resultList != null) {
            return resultList.getValue();
        }
        throw new ConfigNotAvailableException("config with key '" + configKey + "' not found");
    }

    @Transactional
    public void saveValue(String configKey, String value) {
        ConfigValueEntity configValueEntity = new ConfigValueEntity(configKey, value);
        entityManager.get().persist(configValueEntity);
    }

}
