package net.wbz.moba.controlcenter.web.server.config;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import net.wbz.moba.controlcenter.web.shared.config.ConfigNotAvailableException;
import net.wbz.moba.controlcenter.web.shared.config.ConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.ws.rs.Path;

/**
 * @author Daniel Tuerk
 */
@Singleton
@Path("config")
public class ConfigServiceImpl implements ConfigService {
    private static final Logger log = LoggerFactory.getLogger(ConfigServiceImpl.class);

    private final Provider<EntityManager> entityManager;

    @Inject
    public ConfigServiceImpl(Provider<EntityManager> entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public String loadValue(String configKey) throws ConfigNotAvailableException {
        Query typedQuery = entityManager.get().createQuery(
                "SELECT x FROM ConfigValue x where key=" + configKey);
        ConfigValue resultList = (ConfigValue) typedQuery.getSingleResult();
        if (resultList != null) {
            return resultList.getValue();
        }
        throw new ConfigNotAvailableException("config with key '" + configKey + "' not found");
    }

    @Override
    @Transactional
    public void saveValue(String configKey, String value) {
        ConfigValue configValue = new ConfigValue(configKey, value);
        entityManager.get().persist(configValue);
    }

}
