package net.wbz.moba.controlcenter.web.server.persist.device;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import net.wbz.moba.controlcenter.web.server.persist.AbstractDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

/**
 * @author Daniel Tuerk
 */
@Singleton
public class DeviceInfoDao extends AbstractDao<DeviceInfoEntity> {
    private static final Logger LOG = LoggerFactory.getLogger(DeviceInfoDao.class);

    @Inject
    public DeviceInfoDao(Provider<EntityManager> entityManager) {
        super(entityManager);
    }

    @Override
    public DeviceInfoEntity getById(Long id) {
        return (DeviceInfoEntity) getEntityManager().createQuery("select x  FROM DeviceInfo WHERE id = :id")
                .setParameter("id", id).getSingleResult();
    }

    @Transactional
    public List<DeviceInfoEntity> listAll() {
        LOG.info("load constructions");
        Query typedQuery = getEntityManager().createQuery("SELECT x FROM DeviceInfo x");
        List<DeviceInfoEntity> resultList = safeList(typedQuery);
        return resultList;
    }


}
