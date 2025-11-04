package net.wbz.moba.controlcenter.service.track;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.wbz.moba.controlcenter.persist.entity.track.AbstractTrackPartEntity;
import net.wbz.moba.controlcenter.persist.entity.track.BlockStraightEntity;
import net.wbz.moba.controlcenter.persist.entity.track.CurveEntity;
import net.wbz.moba.controlcenter.persist.entity.track.SignalEntity;
import net.wbz.moba.controlcenter.persist.entity.track.StraightEntity;
import net.wbz.moba.controlcenter.persist.entity.track.TurnoutEntity;
import net.wbz.moba.controlcenter.persist.entity.track.UncouplerEntity;
import net.wbz.moba.controlcenter.persist.repository.track.TrackPartRepository;
import net.wbz.moba.controlcenter.service.constrution.ConstructionService;
import net.wbz.moba.controlcenter.shared.constrution.Construction;
import net.wbz.moba.controlcenter.shared.track.model.AbstractTrackPart;
import org.jboss.logging.Logger;

/**
 * @author Daniel Tuerk
 */
@ApplicationScoped
public class TrackProvider {

    private static final Logger LOG = Logger.getLogger(TrackProvider.class);

    /**
     * Cached and transformed entities for track of current {@link Construction}.
     */
    private final Collection<AbstractTrackPart> cachedEntities = new ArrayList<>();
    private final TrackPartRepository trackPartRepository;
    private final TrackPartMapper trackPartMapper;

    @Inject
    public TrackProvider(ConstructionService constructionService,
        TrackPartRepository trackPartRepository,
        TrackPartMapper trackPartMapper) {
        this.trackPartRepository = trackPartRepository;
        this.trackPartMapper = trackPartMapper;

        constructionService.addListener(this::loadData);
    }

    public Collection<AbstractTrackPart> getTrack() {
        return cachedEntities;
    }

    private void loadData(Construction construction) {
        loadTrackBlocksData();
        loadTrackPartData(construction);
    }


    private void loadTrackBlocksData() {
        // TODO migrate
//        cachedTrackBlocks.clear();
//        if (currentConstruction != null) {
//            List<TrackBlockEntity> trackBlocks = trackBlockDao.findByConstructionId(currentConstruction.getId());
//            if (!trackBlocks.isEmpty()) {
//                cachedTrackBlocks.addAll(trackBlockDataMapper.transformSource(trackBlocks));
//            }
//        }
    }

    private void loadTrackPartData(Construction construction) {
        cachedEntities.clear();
        LOG.info("load track parts from db");

        List<AbstractTrackPartEntity> result = trackPartRepository.findByConstructionId(construction.getId());
        LOG.info("return track parts");
        cachedEntities.addAll(result.stream().map(x -> switch (x) {
            case CurveEntity e -> trackPartMapper.toDto(e);
            case TurnoutEntity e -> trackPartMapper.toDto(e);
            case BlockStraightEntity e -> trackPartMapper.toDto(e);
            case SignalEntity e -> trackPartMapper.toDto(e);
            case UncouplerEntity e -> trackPartMapper.toDto(e);
            case StraightEntity e -> trackPartMapper.toDto(e);
            default -> throw new IllegalStateException("Unexpected value: " + x);
        }).toList());

        // TODO migrate (do it here or in a separate class? Originally in TrainManager
//                if (deviceManager.isConnected()) {
//                    registerConsumersByConnectedDeviceForTrackParts();
//                }

    }
}
