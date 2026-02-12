package net.wbz.moba.controlcenter.service.track;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.wbz.moba.controlcenter.EventBroadcaster;
import net.wbz.moba.controlcenter.persist.entity.track.TrackBlockEntity;
import net.wbz.moba.controlcenter.persist.repository.ConstructionRepository;
import net.wbz.moba.controlcenter.persist.repository.track.TrackBlockRepository;
import net.wbz.moba.controlcenter.shared.track.model.TrackBlock;
import net.wbz.moba.controlcenter.shared.track.model.TrackBlockDataChangedEvent;

/**
 * @author Daniel Tuerk
 */
@ApplicationScoped
public class TrackBlockManager {

    @Inject
    TrackBlockRepository trackBlockRepository;
    @Inject
    TrackBlockMapper trackBlockMapper;
    @Inject
    ConstructionRepository constructionRepository;
    @Inject
    EventBroadcaster eventBroadcaster;
    @Inject
    BusDataConfigMapper busDataConfigMapper;
    @Inject
    EntityManager entityManager;

    public List<TrackBlock> fetch(Long constructionId) {
        return trackBlockRepository.findByConstructionId(constructionId).stream()
            .map(trackBlockEntity -> trackBlockMapper.toDto(trackBlockEntity)).collect(
                Collectors.toList());
    }

    @Transactional
    public TrackBlock create(Long constructionId, TrackBlock dto) {
        TrackBlockEntity entity = new TrackBlockEntity();
        entity.name = dto.getName();
        entity.construction = constructionRepository.findById(constructionId);
        entity.feedback = dto.getFeedback();
        entity.blockFunction = busDataConfigMapper.toEntity(dto.getBlockFunction());
        entity.drivingLevelAdjustType = dto.getDrivingLevelAdjustType();
        entity.forwardTargetDrivingLevel = dto.getForwardTargetDrivingLevel();
        entity.backwardTargetDrivingLevel = dto.getBackwardTargetDrivingLevel();

        trackBlockRepository.persist(entity);
        eventBroadcaster.fireEvent(new TrackBlockDataChangedEvent(entity.id));
        return trackBlockMapper.toDto(entity);
    }

    @Transactional
    public void update(Long id, TrackBlock updated) {
        var existing = trackBlockRepository.findById(id);
        if (existing == null) {
            throw new EntityNotFoundException();
        }
        existing.name = updated.getName();
        existing.feedback = updated.getFeedback();
        existing.blockFunction = entityManager.merge(busDataConfigMapper.toEntity(updated.getBlockFunction()));
        existing.drivingLevelAdjustType = updated.getDrivingLevelAdjustType();
        existing.forwardTargetDrivingLevel = updated.getForwardTargetDrivingLevel();
        existing.backwardTargetDrivingLevel = updated.getBackwardTargetDrivingLevel();

        eventBroadcaster.fireEvent(new TrackBlockDataChangedEvent(existing.id));
    }

    @Transactional
    public boolean deleteById(Long id) {
        var state = trackBlockRepository.deleteById(id);
        eventBroadcaster.fireEvent(new TrackBlockDataChangedEvent(id));
        return state;
    }

    public boolean existsById(Long id) {
        return trackBlockRepository.findByIdOptional(id).isPresent();
    }

    public Optional<TrackBlock> getById(Long trackBlockId) {
        return trackBlockRepository.findByIdOptional(trackBlockId)
            .map(trackBlockEntity -> trackBlockMapper.toDto(trackBlockEntity));
    }
}
