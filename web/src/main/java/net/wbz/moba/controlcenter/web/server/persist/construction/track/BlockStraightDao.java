package net.wbz.moba.controlcenter.web.server.persist.construction.track;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.List;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import net.wbz.moba.controlcenter.web.server.persist.AbstractDao;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackBlock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Daniel Tuerk
 */
@Singleton
public class BlockStraightDao extends AbstractDao<BlockStraightEntity> {

    private static final Logger LOG = LoggerFactory.getLogger(BlockStraightDao.class);

    @Inject
    public BlockStraightDao(Provider<EntityManager> entityManager) {
        super(entityManager, BlockStraightEntity.class);
    }

    public void deleteTrackBlock(TrackBlock trackBlock) {
        //TODO
//        List<AbstractTrackPartEntity> byBlockId = trackPartDao.findByBlockId(trackBlock.getId());
//        if (!byBlockId.isEmpty()) {
//
//            for (AbstractTrackPartEntity trackPartEntity : byBlockId) {
//                trackPartEntity.setTrackBlock(null);
//                trackPartDao.update(trackPartEntity);
//            }
//            trackPartDao.flush();
//        }
        throw new RuntimeException("not implemented");
    }
}
