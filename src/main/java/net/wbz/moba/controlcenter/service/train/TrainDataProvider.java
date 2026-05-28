package net.wbz.moba.controlcenter.service.train;

import io.quarkus.cache.CacheInvalidateAll;
import io.quarkus.cache.CacheResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import net.wbz.moba.controlcenter.api.train.TrainDto;
import net.wbz.moba.controlcenter.persist.entity.TrainEntity;
import net.wbz.moba.controlcenter.persist.repository.TrainRepository;
import net.wbz.moba.controlcenter.shared.train.Train;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@ApplicationScoped
public class TrainDataProvider {

    private static final String CACHE = "trains-cache";

    private final TrainRepository trainRepository;
    private final TrainMapper trainMapper;

    public TrainDataProvider(TrainRepository trainRepository, TrainMapper trainMapper) {
        this.trainRepository = trainRepository;
        this.trainMapper = trainMapper;
    }

    /**
     * Load all trains from database and cache the result.
     */
    @CacheResult(cacheName = CACHE)
    public List<Train> getTrains() {
        log.debug("load trains from database");
        return trainRepository.getTrains().stream()
            .map(trainMapper::toDto)
            .collect(Collectors.toList());
    }

    /**
     * Create a new train and invalidate the cache.
     */
    @CacheInvalidateAll(cacheName = CACHE)
    @Transactional
    public Long createTrain(TrainDto dto) {
        TrainEntity entity = new TrainEntity();
        entity.name = dto.name();
        entity.address = dto.address();
        // TODO train functions
        trainRepository.persist(entity);
        return entity.id;
    }

    /**
     * Update an existing train and invalidate the cache.
     */
    @CacheInvalidateAll(cacheName = CACHE)
    @Transactional
    public void updateTrain(Long id, TrainDto updated) {
        TrainEntity existing = trainRepository.findById(id);
        if (existing == null) {
            throw new EntityNotFoundException();
        }
        existing.name = updated.name();
        existing.address = updated.address();
        // TODO train functions
    }

    /**
     * Delete a train by id and invalidate the cache.
     */
    @CacheInvalidateAll(cacheName = CACHE)
    @Transactional
    public boolean deleteTrain(Long id) {
        return trainRepository.deleteById(id);
    }

    public Optional<Train> getById(Long id) {
        return getTrains().stream()
            .filter(train -> train.getId().equals(id))
            .findFirst();
    }
}
