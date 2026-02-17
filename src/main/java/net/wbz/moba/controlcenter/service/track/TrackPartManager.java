package net.wbz.moba.controlcenter.service.track;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import net.wbz.moba.controlcenter.EventBroadcaster;
import net.wbz.moba.controlcenter.persist.entity.track.AbstractTrackPartEntity;
import net.wbz.moba.controlcenter.persist.entity.track.BlockStraightEntity;
import net.wbz.moba.controlcenter.persist.entity.track.BusDataConfigurationEntity;
import net.wbz.moba.controlcenter.persist.entity.track.HasToggleFunctionEntity;
import net.wbz.moba.controlcenter.persist.entity.track.SignalEntity;
import net.wbz.moba.controlcenter.persist.entity.track.TrackBlockEntity;
import net.wbz.moba.controlcenter.persist.repository.track.TrackPartRepository;
import net.wbz.moba.controlcenter.shared.track.model.AbstractTrackPart;
import net.wbz.moba.controlcenter.shared.track.model.BlockStraight;
import net.wbz.moba.controlcenter.shared.track.model.BusDataConfiguration;
import net.wbz.moba.controlcenter.shared.track.model.HasToggleFunction;
import net.wbz.moba.controlcenter.shared.track.model.Signal;
import net.wbz.moba.controlcenter.shared.track.model.TrackBlock;
import net.wbz.moba.controlcenter.shared.track.model.TrackPartDataChangedEvent;

/**
 * @author Daniel Tuerk
 */
@ApplicationScoped
public class TrackPartManager {

    @Inject
    TrackProvider trackProvider;
    @Inject
    TrackPartRepository trackPartRepository;
    @Inject
    EventBroadcaster eventBroadcaster;
    @Inject
    BusDataConfigMapper busDataConfigMapper;
    @Inject
    EntityManager entityManager;
    @Inject
    TrackBlockMapper trackBlockMapper;

    @Transactional
    public void update(Long id, AbstractTrackPart updated) {
        var existing = trackPartRepository.findByIdOptional(id)
            .orElseThrow(EntityNotFoundException::new);
        switch (updated) {
            case Signal signal:
                updateSignal(signal, existing);
                break;
            case HasToggleFunction hasToggleFunction:
                updateToggleFunction(hasToggleFunction, existing);
                break;
            case BlockStraight blockStraight:
                updateBlockStraight(blockStraight, existing);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + updated);
        }
    }

    @Transactional
    public boolean deleteById(Long id) {
        var state = trackPartRepository.deleteById(id);
        eventBroadcaster.fireEvent(new TrackPartDataChangedEvent(id));
        return state;
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

            signalEntity.breakingBlock = getMergedTrackBlockEntity(signal.getBreakingBlock());
            signalEntity.enteringBlock = getMergedTrackBlockEntity(signal.getEnteringBlock());
            signalEntity.monitoringBlock = getMergedTrackBlockEntity(signal.getMonitoringBlock());
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

    private void persistTrackPart(AbstractTrackPartEntity existing) {
        trackPartRepository.persist(existing);
        trackProvider.markDirty();
        eventBroadcaster.fireEvent(new TrackPartDataChangedEvent(existing.id));
    }

}
