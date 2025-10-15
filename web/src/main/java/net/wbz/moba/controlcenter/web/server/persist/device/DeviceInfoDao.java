package net.wbz.moba.controlcenter.web.server.persist.device;

import java.util.List;

import javax.inject.Provider;
import jakarta.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import net.wbz.moba.controlcenter.web.server.persist.AbstractDao;

/**
 * @author Daniel Tuerk
 */
@Singleton
public class DeviceInfoDao implements PanacheRepository<DeviceInfoEntity> {
    private static final Logger LOG = LoggerFactory.getLogger(DeviceInfoDao.class);

    @Inject
    public DeviceInfoDao(Provider<EntityManager> entityManager) {
        super(entityManager, DeviceInfoEntity.class);
    }

    // @Override
    // public DeviceInfoEntity getById(Long id) {
    // return (DeviceInfoEntity) getEntityManager().createQuery("SELECT x FROM device_info x WHERE x.id = :id")
    // .setParameter("id", id).getSingleResult();
    // }

    public List<DeviceInfoEntity> listAll() {
        LOG.info("load constructions");
        return getEntityManager().createQuery("SELECT x FROM DEVICE_INFO x", DeviceInfoEntity.class).getResultList();
    }

}
