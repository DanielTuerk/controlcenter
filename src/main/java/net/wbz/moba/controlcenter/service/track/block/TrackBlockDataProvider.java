package net.wbz.moba.controlcenter.service.track.block;

import io.quarkus.cache.CacheInvalidateAll;
import io.quarkus.cache.CacheResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import net.wbz.moba.controlcenter.persist.entity.track.TrackBlockEntity;
import net.wbz.moba.controlcenter.persist.repository.ConstructionRepository;
import net.wbz.moba.controlcenter.persist.repository.track.TrackBlockRepository;
import net.wbz.moba.controlcenter.service.track.BusDataConfigMapper;
import net.wbz.moba.controlcenter.shared.track.model.TrackBlock;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class TrackBlockDataProvider {
    private static final String CACHE = "track-block-cache";

    @Inject
    TrackBlockRepository trackBlockRepository;
    @Inject
    BusDataConfigMapper busDataConfigMapper;
    @Inject
    ConstructionRepository constructionRepository;
    @Inject
    EntityManager entityManager;

    @CacheResult(cacheName = CACHE)
    public List<TrackBlockEntity> load(Long constructionId) {
        return trackBlockRepository.findByConstructionId(constructionId);
    }

    public Optional<TrackBlockEntity> getById(Long trackBlockId) {
        return trackBlockRepository.findByIdOptional(trackBlockId);
    }

    @CacheInvalidateAll(cacheName = CACHE)
    @Transactional
    public TrackBlockEntity create(Long constructionId, TrackBlock dto) {
        var entity = new TrackBlockEntity();
        entity.name = dto.getName();
        entity.construction = constructionRepository.findById(constructionId);
        entity.feedback = dto.getFeedback();
        entity.blockFunction = busDataConfigMapper.toEntity(dto.getBlockFunction());
        entity.drivingLevelAdjustType = dto.getDrivingLevelAdjustType();
        entity.forwardTargetDrivingLevel = dto.getForwardTargetDrivingLevel();
        entity.backwardTargetDrivingLevel = dto.getBackwardTargetDrivingLevel();

        trackBlockRepository.persist(entity);
        return entity;
    }

    @CacheInvalidateAll(cacheName = CACHE)
    @Transactional
    public TrackBlockEntity update(Long id, TrackBlock updated) {
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
        return existing;
    }

    @CacheInvalidateAll(cacheName = CACHE)
    @Transactional
    public boolean deleteById(Long id) {
        return trackBlockRepository.deleteById(id);
    }
}
