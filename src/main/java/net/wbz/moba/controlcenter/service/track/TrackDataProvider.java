package net.wbz.moba.controlcenter.service.track;

import io.quarkus.cache.CacheInvalidateAll;
import io.quarkus.cache.CacheResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import net.wbz.moba.controlcenter.api.track.ChangeTrackDto;
import net.wbz.moba.controlcenter.persist.entity.track.AbstractTrackPartEntity;
import net.wbz.moba.controlcenter.persist.entity.track.BlockStraightEntity;
import net.wbz.moba.controlcenter.persist.entity.track.BusDataConfigurationEntity;
import net.wbz.moba.controlcenter.persist.entity.track.CurveEntity;
import net.wbz.moba.controlcenter.persist.entity.track.GridPositionEntity;
import net.wbz.moba.controlcenter.persist.entity.track.HasToggleFunctionEntity;
import net.wbz.moba.controlcenter.persist.entity.track.SignalEntity;
import net.wbz.moba.controlcenter.persist.entity.track.StraightEntity;
import net.wbz.moba.controlcenter.persist.entity.track.TrackBlockEntity;
import net.wbz.moba.controlcenter.persist.entity.track.TurnoutEntity;
import net.wbz.moba.controlcenter.persist.entity.track.UncouplerEntity;
import net.wbz.moba.controlcenter.persist.repository.track.GridPositionRepository;
import net.wbz.moba.controlcenter.persist.repository.track.TrackPartRepository;
import net.wbz.moba.controlcenter.service.track.block.TrackBlockMapper;
import net.wbz.moba.controlcenter.shared.constrution.CurrentConstructionChangeEvent;
import net.wbz.moba.controlcenter.shared.track.model.AbstractTrackPart;
import net.wbz.moba.controlcenter.shared.track.model.BlockStraight;
import net.wbz.moba.controlcenter.shared.track.model.BusDataConfiguration;
import net.wbz.moba.controlcenter.shared.track.model.Curve;
import net.wbz.moba.controlcenter.shared.track.model.GridPosition;
import net.wbz.moba.controlcenter.shared.track.model.HasToggleFunction;
import net.wbz.moba.controlcenter.shared.track.model.Signal;
import net.wbz.moba.controlcenter.shared.track.model.Straight;
import net.wbz.moba.controlcenter.shared.track.model.TrackBlock;
import net.wbz.moba.controlcenter.shared.track.model.Turnout;
import net.wbz.moba.controlcenter.shared.track.model.Uncoupler;

import java.util.Collection;

@Slf4j
@ApplicationScoped
public class TrackDataProvider {
    private static final String CACHE = "trackpart-cache";

    private final TrackPartRepository trackPartRepository;
    private final TrackPartMapper trackPartMapper;
    private final BusDataConfigMapper busDataConfigMapper;
    private final EntityManager entityManager;
    private final TrackBlockMapper trackBlockMapper;
    private final GridPositionRepository gridPositionRepository;

    public TrackDataProvider(TrackPartRepository trackPartRepository, TrackPartMapper trackPartMapper,
                             BusDataConfigMapper busDataConfigMapper, EntityManager entityManager,
                             TrackBlockMapper trackBlockMapper, GridPositionRepository gridPositionRepository) {
        this.trackPartRepository = trackPartRepository;
        this.trackPartMapper = trackPartMapper;
        this.busDataConfigMapper = busDataConfigMapper;
        this.entityManager = entityManager;
        this.trackBlockMapper = trackBlockMapper;
        this.gridPositionRepository = gridPositionRepository;
    }

    @CacheInvalidateAll(cacheName = CACHE)
    public void onCurrentConstructionChanged(@Observes CurrentConstructionChangeEvent event) {
        log.info("current construction changed for ID: {}, refreshing track...", event.construction().getId());
    }

    @CacheResult(cacheName = CACHE)
    public Collection<AbstractTrackPart> loadTrack(long constructionId) {
        log.info("load track parts from db");

        var result = trackPartRepository.findByConstructionId(constructionId);
        log.info("return track parts ({})", result.size());
        return result.stream()
            .map(entity -> switch (entity) {
                case CurveEntity e -> trackPartMapper.toDto(e);
                case TurnoutEntity e -> trackPartMapper.toDto(e);
                case BlockStraightEntity e -> trackPartMapper.toDto(e);
                case SignalEntity e -> trackPartMapper.toDto(e);
                case UncouplerEntity e -> trackPartMapper.toDto(e);
                case StraightEntity e -> trackPartMapper.toDto(e);
                default -> throw new IllegalStateException("Unexpected value: " + entity);
            })
            .toList();
    }

    @CacheInvalidateAll(cacheName = CACHE)
    @Transactional
    public void update(Long id, AbstractTrackPart updated) {
        var existing = trackPartRepository.findByIdOptional(id)
            .orElseThrow(EntityNotFoundException::new);
        switch (updated) {
            case Signal signal:
                updateSignal(signal, existing);
                break;
            case HasToggleFunction hasToggleFunction:
                hasToggleFunction.getToggleFunction().setBus(1);
                updateToggleFunction(hasToggleFunction, existing);
                break;
            case BlockStraight blockStraight:
                updateBlockStraight(blockStraight, existing);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + updated);
        }
    }

    @CacheInvalidateAll(cacheName = CACHE)
    @Transactional
    public boolean deleteById(Long id) {
        return trackPartRepository.deleteById(id);
    }

    @CacheInvalidateAll(cacheName = CACHE)
    @Transactional
    public void add(ChangeTrackDto.Add action, long constructionId) {
        action.trackPart().setId(null);
        var entity = switch (action.trackPart()) {
            case BlockStraight blockStraight -> {
                if (blockStraight.getBlockLength() == 0) blockStraight.setBlockLength(1);
                yield trackPartMapper.toEntity(blockStraight);
            }
            case Signal signal -> trackPartMapper.toEntity(signal);
            case Uncoupler uncoupler -> trackPartMapper.toEntity(uncoupler);
            case Turnout turnout -> trackPartMapper.toEntity(turnout);
            case Straight straight -> trackPartMapper.toEntity(straight);
            case Curve curve -> trackPartMapper.toEntity(curve);
            default -> throw new IllegalStateException("Unexpected track part type: " + action.trackPart());
        };
        entity.id = null; // TODO ugly: set to null to trigger insert and ignore the negative temp id from UI
        entity.constructionId = constructionId;
        entity.gridPosition.constructionId = constructionId;
        trackPartRepository.persist(entity);
    }

    @CacheInvalidateAll(cacheName = CACHE)
    @Transactional
    public void move(long trackPartEntityId, GridPosition newGridPosition) {
        var trackPartEntity = fetchEntityById(trackPartEntityId);
        if (trackPartEntity.gridPosition.x == newGridPosition.getX()
            && trackPartEntity.gridPosition.y == newGridPosition.getY()) {
            return;
        }

        var existingGridPosEntity = gridPositionRepository.findByGridPosition(newGridPosition);

        if (existingGridPosEntity == null) {
            // non-existing grid position can be applied
            var gridPosition = new GridPositionEntity();
            gridPosition.x = newGridPosition.getX();
            gridPosition.y = newGridPosition.getY();
            gridPosition.constructionId = trackPartEntity.constructionId;
            trackPartEntity.gridPosition = gridPosition;

            trackPartRepository.persist(trackPartEntity);
        } else {
            trackPartEntity.gridPosition = existingGridPosEntity;
            trackPartRepository.persist(trackPartEntity);
        }
    }

    @CacheInvalidateAll(cacheName = CACHE)
    @Transactional
    public void rotate(long trackPartId, String value) {
        var trackPartEntity = fetchEntityById(trackPartId);
        switch (trackPartEntity) {
            case StraightEntity straightEntity:
                straightEntity.direction = Straight.DIRECTION.valueOf(value);
                trackPartRepository.persist(straightEntity);
                break;
            case CurveEntity curveEntity:
                curveEntity.direction = Curve.DIRECTION.valueOf(value);
                trackPartRepository.persist(curveEntity);
                break;
            case TurnoutEntity turnoutEntity:
                turnoutEntity.currentPresentation = Turnout.PRESENTATION.valueOf(value);
                trackPartRepository.persist(turnoutEntity);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + trackPartEntity);
        }
    }

    private void updateSignal(Signal signal, AbstractTrackPartEntity existing) {
        if (existing instanceof SignalEntity signalEntity) {
            signalEntity.type = signal.getType();

            signalEntity.signalConfigGreen1 = getMergedBusDataConfig(signal.getSignalConfigGreen1());
            signalEntity.signalConfigGreen2 = getMergedBusDataConfig(signal.getSignalConfigGreen2());
            signalEntity.signalConfigRed1 = getMergedBusDataConfig(signal.getSignalConfigRed1());
            signalEntity.signalConfigRed2 = getMergedBusDataConfig(signal.getSignalConfigRed2());
            signalEntity.signalConfigYellow1 = getMergedBusDataConfig(signal.getSignalConfigYellow1());
            signalEntity.signalConfigYellow2 = getMergedBusDataConfig(signal.getSignalConfigYellow2());
            signalEntity.signalConfigWhite = getMergedBusDataConfig(signal.getSignalConfigWhite());

            signalEntity.stopBlock = getMergedTrackBlockEntity(signal.getStopBlock());

            persistTrackPart(signalEntity);
        } else {
            throw new IllegalStateException(
                "%s(%d) is no %s".formatted(existing.getClass().getSimpleName(),
                    existing.id, SignalEntity.class.getSimpleName()));
        }
    }

    private void updateToggleFunction(HasToggleFunction hasToggleFunction, AbstractTrackPartEntity existing) {
        if (existing instanceof HasToggleFunctionEntity) {
            ((HasToggleFunctionEntity) existing).setToggleFunctionConfiguration(
                getMergedBusDataConfig(hasToggleFunction.getToggleFunction()));
            persistTrackPart(existing);
        } else {
            throw new IllegalStateException(
                "no toggle-function of: %s(%d)".formatted(existing.getClass().getSimpleName(),
                    existing.id));
        }
    }

    private void updateBlockStraight(BlockStraight blockStraight, AbstractTrackPartEntity existing) {
        if (existing instanceof BlockStraightEntity entity) {
            entity.blockLength = blockStraight.getBlockLength();
            entity.leftTrackBlock = getMergedTrackBlockEntity(blockStraight.getLeftTrackBlock());
            entity.middleTrackBlock = getMergedTrackBlockEntity(
                blockStraight.getMiddleTrackBlock());
            entity.rightTrackBlock = getMergedTrackBlockEntity(blockStraight.getRightTrackBlock());
            persistTrackPart(entity);
        } else {
            throw new IllegalStateException(
                "%s(%d) is no %s".formatted(existing.getClass().getSimpleName(),
                    existing.id, BlockStraightEntity.class.getSimpleName()));
        }
    }

    private BusDataConfigurationEntity getMergedBusDataConfig(BusDataConfiguration busDataConfiguration) {
        return entityManager.merge(busDataConfigMapper.toEntity(busDataConfiguration));
    }

    private TrackBlockEntity getMergedTrackBlockEntity(TrackBlock trackBlock) {
        return trackBlock == null ? null : entityManager.merge(trackBlockMapper.toEntity(trackBlock));
    }

    private void persistTrackPart(AbstractTrackPartEntity entity) {
        trackPartRepository.persist(entity);
    }

    private AbstractTrackPartEntity fetchEntityById(Long trackPartId) {
        return trackPartRepository.findByIdOptional(trackPartId)
            .orElseThrow(() -> new IllegalStateException("no track part with id " + trackPartId));
    }
}
