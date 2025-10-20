package net.wbz.moba.controlcenter.web.server.persist.construction.track;



import java.util.List;
import javax.inject.Provider;
import jakarta.persistence.EntityManager;
import net.wbz.moba.controlcenter.web.server.persist.AbstractDao;
import net.wbz.moba.controlcenter.shared.track.model.TrackBlock;
import org.jboss.logging.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Daniel Tuerk
 */
@Singleton
public class BlockStraightDao implements PanacheRepository<BlockStraightEntity> {

    private static final Logger LOG = Logger.getLogger(BlockStraightDao.class);

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
