package net.wbz.moba.controlcenter.service.track;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import net.wbz.moba.controlcenter.EventBroadcaster;
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
import net.wbz.moba.controlcenter.shared.track.model.TrackChangedEvent;
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
    private final Collection<AbstractTrackPart> cachedEntities = Collections.synchronizedList(new ArrayList<>());
    private final TrackPartRepository trackPartRepository;
    private final TrackPartMapper trackPartMapper;
    private final EventBroadcaster eventBroadcaster;
    private final ConstructionService constructionService;

    @Inject
    public TrackProvider(ConstructionService constructionService,
        TrackPartRepository trackPartRepository,
        TrackPartMapper trackPartMapper,
        EventBroadcaster eventBroadcaster) {
        this.trackPartRepository = trackPartRepository;
        this.trackPartMapper = trackPartMapper;
        this.eventBroadcaster = eventBroadcaster;

        constructionService.addListener(construction -> {
            loadData(construction);
            eventBroadcaster.fireEvent(new TrackChangedEvent(false));
        });
        this.constructionService = constructionService;
    }

    public Collection<AbstractTrackPart> getTrack() {
        if (cachedEntities.isEmpty()) {
            constructionService.getCurrentConstruction().ifPresent(this::loadData);
        }
        return cachedEntities;
    }

    public Optional<AbstractTrackPart> getTrackPart(Long trackPartId) {
        return getTrack().stream().filter(x-> x.getId().equals(trackPartId)).findAny();
    }

    private void loadData(Construction construction) {
        loadTrackBlocksData();
        loadTrackPartData(construction);
    }

    private void loadTrackBlocksData() {
        // TODO migrate / really needed?
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
        LOG.infof("return track parts (%d)", result.size());
        cachedEntities.addAll(result.stream().map(x -> switch (x) {
            case CurveEntity e -> trackPartMapper.toDto(e);
            case TurnoutEntity e -> trackPartMapper.toDto(e);
            case BlockStraightEntity e -> trackPartMapper.toDto(e);
            case SignalEntity e -> trackPartMapper.toDto(e);
            case UncouplerEntity e -> trackPartMapper.toDto(e);
            case StraightEntity e -> trackPartMapper.toDto(e);
            default -> throw new IllegalStateException("Unexpected value: " + x);
        }).toList());
    }

    public synchronized void markDirty() {
        cachedEntities.clear();
        eventBroadcaster.fireEvent(new TrackChangedEvent(true));
    }
}
