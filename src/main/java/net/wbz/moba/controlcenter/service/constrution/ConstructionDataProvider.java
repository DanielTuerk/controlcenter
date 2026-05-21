package net.wbz.moba.controlcenter.service.constrution;

import io.quarkus.cache.CacheInvalidateAll;
import io.quarkus.cache.CacheResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import net.wbz.moba.controlcenter.api.construction.ConstructionDto;
import net.wbz.moba.controlcenter.persist.entity.ConstructionEntity;
import net.wbz.moba.controlcenter.persist.repository.ConstructionRepository;

import java.util.List;

@Slf4j
@ApplicationScoped
public class ConstructionDataProvider {

    private static final String CACHE = "constructions-cache";

    private final ConstructionRepository constructionRepository;

    public ConstructionDataProvider(ConstructionRepository constructionRepository) {
        this.constructionRepository = constructionRepository;
    }

    /**
     * Load all constructions from database and cache the result.
     */
    @CacheResult(cacheName = CACHE)
    public List<ConstructionEntity> getConstructions() {
        log.debug("load constructions from database");
        return constructionRepository.listAll();
    }

    /**
     * Create a new construction and invalidate the cache.
     */
    @CacheInvalidateAll(cacheName = CACHE)
    @Transactional
    public ConstructionEntity createConstruction(ConstructionDto construction) {
        var entity = new ConstructionEntity();
        entity.name = construction.name();
        constructionRepository.persist(entity);
        return entity;
    }

    /**
     * Update an existing construction and invalidate the cache.
     */
    @CacheInvalidateAll(cacheName = CACHE)
    @Transactional
    public ConstructionEntity updateConstruction(Long id, ConstructionDto updated) {
        var existing = constructionRepository.findById(id);
        if (existing == null) {
            throw new EntityNotFoundException();
        }
        existing.name = updated.name();
        return existing;
    }

    /**
     * Delete a construction by id and invalidate the cache.
     */
    @CacheInvalidateAll(cacheName = CACHE)
    @Transactional
    public boolean deleteConstruction(Long id) {
        return constructionRepository.deleteById(id);
    }
}
