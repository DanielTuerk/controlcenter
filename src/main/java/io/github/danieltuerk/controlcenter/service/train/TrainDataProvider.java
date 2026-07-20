package io.github.danieltuerk.controlcenter.service.train;

import io.github.danieltuerk.controlcenter.api.train.TrainDto;
import io.github.danieltuerk.controlcenter.persist.entity.TrainEntity;
import io.github.danieltuerk.controlcenter.persist.entity.TrainFunctionEntity;
import io.github.danieltuerk.controlcenter.persist.entity.track.BusDataConfigurationEntity;
import io.github.danieltuerk.controlcenter.persist.repository.TrainFunctionRepository;
import io.github.danieltuerk.controlcenter.persist.repository.TrainRepository;
import io.github.danieltuerk.controlcenter.shared.train.Train;
import io.github.danieltuerk.controlcenter.shared.train.TrainFunction;
import io.quarkus.cache.CacheInvalidateAll;
import io.quarkus.cache.CacheResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@ApplicationScoped
public class TrainDataProvider {

    private static final String CACHE = "trains-cache";

    private final TrainRepository trainRepository;
    private final TrainMapper trainMapper;
    private final TrainFunctionRepository trainFunctionRepository;

    public TrainDataProvider(TrainRepository trainRepository, TrainMapper trainMapper, TrainFunctionRepository trainFunctionRepository) {
        this.trainRepository = trainRepository;
        this.trainMapper = trainMapper;
        this.trainFunctionRepository = trainFunctionRepository;
    }

    /**
     * Load all trains from the database and cache the result.
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
        if (dto.functions() != null) {
            entity.functions = Stream.of(dto.functions())
                .map(function -> createTrainFunction(function, entity))
                .collect(Collectors.toSet());
        }
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

        updateTrainFunctions(updated, existing);

        trainRepository.persist(existing);
    }

    private void updateTrainFunctions(TrainDto updated, TrainEntity existing) {
        if (updated.functions() == null) {
            existing.functions.clear();
            return;
        }

        final var updatedExistingIds = Stream.of(updated.functions())
            .map(TrainFunction::getId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

        // remove deleted ones
        final var toDelete = existing.functions.stream()
            .filter(function -> !updatedExistingIds.contains(function.id))
            .collect(Collectors.toSet());
        existing.functions.removeAll(toDelete);
        toDelete.forEach(trainFunctionRepository::delete);

        // update exiting
        existing.functions.forEach(funcToUpdate ->
            Stream.of(updated.functions())
                .filter(tf -> tf.getId().equals(funcToUpdate.id))
                .findFirst()
                .ifPresent(trainFunction -> {
                    funcToUpdate.alias = trainFunction.getAlias();
                    funcToUpdate.configuration.address = trainFunction.getConfiguration().getAddress();
                    funcToUpdate.configuration.bit = trainFunction.getConfiguration().getBit();
                    funcToUpdate.configuration.bitState = trainFunction.getConfiguration().getBitState();
                })
        );

        // add new ones
        existing.functions.addAll(Stream.of(updated.functions())
            .filter(function -> function.getId() == null)
            .map(function -> createTrainFunction(function, existing))
            .collect(Collectors.toSet()));
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

    private TrainFunctionEntity createTrainFunction(TrainFunction function, TrainEntity train) {
        var busDataConfigEntity = new BusDataConfigurationEntity();
        busDataConfigEntity.bus = 0;
        busDataConfigEntity.address = function.getConfiguration().getAddress();
        busDataConfigEntity.bit = function.getConfiguration().getBit();
        busDataConfigEntity.bitState = function.getConfiguration().getBitState();

        final var trainFunctionEntity = new TrainFunctionEntity();
        trainFunctionEntity.alias = function.getAlias();
        trainFunctionEntity.configuration = busDataConfigEntity;
        trainFunctionEntity.train = train;

        trainFunctionRepository.persist(trainFunctionEntity);
        return trainFunctionEntity;
    }
}
