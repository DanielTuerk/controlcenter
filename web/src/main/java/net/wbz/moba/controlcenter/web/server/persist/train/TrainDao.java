package net.wbz.moba.controlcenter.web.server.persist.train;

import com.google.inject.persist.Transactional;
import net.wbz.moba.controlcenter.web.server.persist.AbstractDao;
import net.wbz.moba.controlcenter.web.server.web.train.TrainException;

import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

/**
 * @author Daniel Tuerk
 */
public class TrainDao extends AbstractDao<TrainEntity> {

    public TrainDao(Provider<EntityManager> entityManager) {
        super(entityManager);
    }

    @Override
    public TrainEntity getById(Long id) {
        return (TrainEntity) getEntityManager().createQuery("select x  FROM train WHERE id = :id")
                .setParameter("id", id).getSingleResult();
    }


    public List<TrainEntity> getTrains() {
        return safeList(getEntityManager().createQuery("SELECT x FROM train x"));
    }

    public TrainEntity getTrainByAddress(int address) throws TrainException {
        Query typedQuery = getEntityManager().createQuery(
                "SELECT x FROM train x where address=:address");
        typedQuery.setParameter("address", address);
        TrainEntity train = (TrainEntity) typedQuery.getSingleResult();
        if (train != null) {
            return train;
        }
        throw new TrainException(String.format("no train for address %d found!", address));
    }


    public TrainEntity getTrainById(long trainId) throws TrainException {
        Query typedQuery = getEntityManager().createQuery(
                "SELECT x FROM train x where id=:id");
        typedQuery.setParameter("id", trainId);
        TrainEntity train = (TrainEntity) typedQuery.getSingleResult();
        if (train != null) {
            return train;
        }
        throw new TrainException(String.format("no train for id %d found!", trainId));
    }


    @Transactional
    public void deleteTrain(long trainId) throws TrainException {
        delete(getTrainById(trainId));
    }

}
