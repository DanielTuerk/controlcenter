package net.wbz.moba.controlcenter.web.server.persist.scenario;

import java.util.List;

import javax.inject.Provider;
import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import net.wbz.moba.controlcenter.web.server.persist.AbstractDao;

/**
 * @author Daniel Tuerk
 */
@Singleton
public class ScenarioHistoryDao extends AbstractDao<ScenarioHistoryEntity> {
    private static final Logger LOG = LoggerFactory.getLogger(ScenarioHistoryDao.class);

    @Inject
    public ScenarioHistoryDao(Provider<EntityManager> entityManager) {
        super(entityManager, ScenarioHistoryEntity.class);
    }

    public List<ScenarioHistoryEntity> listAll() {
        return getEntityManager().createQuery("SELECT x FROM SCENARIO_HISTORY x", ScenarioHistoryEntity.class)
                .getResultList();
    }

}
