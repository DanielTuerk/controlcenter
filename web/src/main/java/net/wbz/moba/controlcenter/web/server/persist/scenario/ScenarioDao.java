package net.wbz.moba.controlcenter.web.server.persist.scenario;

import java.util.List;

import javax.inject.Provider;
import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import net.wbz.moba.controlcenter.web.server.persist.AbstractDao;
import net.wbz.moba.controlcenter.web.server.persist.device.DeviceInfoEntity;

/**
 * @author Daniel Tuerk
 */
@Singleton
public class ScenarioDao extends AbstractDao<ScenarioEntity> {
    private static final Logger LOG = LoggerFactory.getLogger(ScenarioDao.class);

    @Inject
    public ScenarioDao(Provider<EntityManager> entityManager) {
        super(entityManager, ScenarioEntity.class);
    }

    public List<ScenarioEntity> listAll() {
        return getEntityManager().createQuery("SELECT x FROM SCENARIO x", ScenarioEntity.class).getResultList();
    }

}
