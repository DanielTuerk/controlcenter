package io.github.danieltuerk.controlcenter.service.constrution;

import io.github.danieltuerk.controlcenter.api.construction.ConstructionDto;
import io.github.danieltuerk.controlcenter.persist.entity.ConstructionEntity;
import io.github.danieltuerk.controlcenter.persist.repository.ConstructionRepository;
import io.github.danieltuerk.controlcenter.persist.repository.RouteRepository;
import io.github.danieltuerk.controlcenter.persist.repository.ScenarioRepository;
import io.github.danieltuerk.controlcenter.service.track.TrackDataProvider;
import io.github.danieltuerk.controlcenter.service.track.block.TrackBlockDataProvider;
import io.github.danieltuerk.controlcenter.shared.constrution.Construction;
import io.quarkus.cache.CacheInvalidateAll;
import io.quarkus.cache.CacheResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@ApplicationScoped
public class ConstructionDataProvider {

    private static final String CACHE = "constructions-cache";

    private final ConstructionRepository constructionRepository;
    private final ConstructionMapper constructionMapper;
    private final TrackDataProvider trackDataProvider;
    private final TrackBlockDataProvider trackBlockDataProvider;
    private final RouteRepository routeRepository;
    private final ScenarioRepository scenarioRepository;

    public ConstructionDataProvider(ConstructionRepository constructionRepository,
                                    ConstructionMapper constructionMapper, TrackDataProvider trackDataProvider,
                                    TrackBlockDataProvider trackBlockDataProvider, RouteRepository routeRepository,
                                    ScenarioRepository scenarioRepository) {
        this.constructionRepository = constructionRepository;
        this.constructionMapper = constructionMapper;
        this.trackDataProvider = trackDataProvider;
        this.trackBlockDataProvider = trackBlockDataProvider;
        this.routeRepository = routeRepository;
        this.scenarioRepository = scenarioRepository;
    }

    /**
     * Load all constructions from database and cache the result.
     */
    @CacheResult(cacheName = CACHE)
    public List<Construction> getConstructions() {
        log.debug("load constructions from database");
        return constructionRepository.listAll().stream()
            .map(constructionMapper::toDto)
            .collect(Collectors.toList());
    }

    /**
     * Create a new construction and invalidate the cache.
     */
    @CacheInvalidateAll(cacheName = CACHE)
    @Transactional
    public Long createConstruction(ConstructionDto construction) {
        var entity = new ConstructionEntity();
        entity.name = construction.name();
        constructionRepository.persist(entity);
        return entity.id;
    }

    /**
     * Update an existing construction and invalidate the cache.
     */
    @CacheInvalidateAll(cacheName = CACHE)
    @Transactional
    public void updateConstruction(Long id, ConstructionDto updated) {
        var existing = constructionRepository.findById(id);
        if (existing == null) {
            throw new EntityNotFoundException();
        }
        existing.name = updated.name();
        constructionRepository.persist(existing);
    }

    /**
     * Delete a construction by id and invalidate the cache.
     */
    @CacheInvalidateAll(cacheName = CACHE)
    @Transactional
    public boolean deleteConstruction(Long id) {
        routeRepository.listAll(id).stream()
                .map(x -> x.id)
                .forEach(routeRepository::deleteById);

        scenarioRepository.listAll(id).stream()
                .map(x -> x.id)
                .forEach(scenarioRepository::deleteById);

        trackBlockDataProvider.load(id)
                .stream().map(x -> x.id)
                .forEach(trackBlockDataProvider::deleteById);

        trackBlockDataProvider.load(id).stream()
                .map(x -> x.id)
                .forEach(trackDataProvider::deleteById);

        return constructionRepository.deleteById(id);
    }

    public Optional<Construction> getConstruction(Long id) {
        return getConstructions().stream()
            .filter(c -> c.getId().equals(id))
            .findFirst();
    }
}
