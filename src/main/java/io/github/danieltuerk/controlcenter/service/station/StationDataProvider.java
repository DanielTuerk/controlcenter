package io.github.danieltuerk.controlcenter.service.station;

import io.github.danieltuerk.controlcenter.api.station.StationDto;
import io.github.danieltuerk.controlcenter.persist.entity.StationEntity;
import io.github.danieltuerk.controlcenter.persist.entity.StationPlatformEntity;
import io.github.danieltuerk.controlcenter.persist.entity.track.TrackBlockEntity;
import io.github.danieltuerk.controlcenter.persist.repository.StationPlatformRepository;
import io.github.danieltuerk.controlcenter.persist.repository.StationRepository;
import io.github.danieltuerk.controlcenter.persist.repository.track.TrackBlockRepository;
import io.github.danieltuerk.controlcenter.shared.station.Station;
import io.quarkus.cache.CacheInvalidateAll;
import io.quarkus.cache.CacheResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@ApplicationScoped
public class StationDataProvider {

    private static final String CACHE = "stations-cache";

    private final StationRepository stationRepository;
    private final StationPlatformRepository stationPlatformRepository;
    private final TrackBlockRepository trackBlockRepository;
    private final StationMapper stationMapper;

    public StationDataProvider(StationRepository stationRepository,
                                StationPlatformRepository stationPlatformRepository,
                                TrackBlockRepository trackBlockRepository,
                                StationMapper stationMapper) {
        this.stationRepository = stationRepository;
        this.stationPlatformRepository = stationPlatformRepository;
        this.trackBlockRepository = trackBlockRepository;
        this.stationMapper = stationMapper;
    }

    /**
     * Load all stations from database and cache the result.
     */
    @CacheResult(cacheName = CACHE)
    public List<Station> getStations() {
        log.debug("load stations from database");
        return stationRepository.listAll().stream()
            .map(stationMapper::toDto)
            .collect(Collectors.toList());
    }

    /**
     * Create a new station and invalidate the cache.
     */
    @CacheInvalidateAll(cacheName = CACHE)
    @Transactional
    public Long createStation(StationDto station) {
        var entity = new StationEntity();
        entity.name = station.name();
        entity.platforms = new ArrayList<>();
        stationRepository.persist(entity);

        applyPlatforms(entity, station.platforms());
        return entity.id;
    }

    /**
     * Update an existing station and invalidate the cache.
     */
    @CacheInvalidateAll(cacheName = CACHE)
    @Transactional
    public void updateStation(Long id, StationDto updated) {
        var existing = stationRepository.findById(id);
        if (existing == null) {
            throw new EntityNotFoundException();
        }
        existing.name = updated.name();
        applyPlatforms(existing, updated.platforms());
    }

    /**
     * Delete a station (including its platforms) by id and invalidate the cache.
     */
    @CacheInvalidateAll(cacheName = CACHE)
    @Transactional
    public boolean deleteStation(Long id) {
        var existing = stationRepository.findById(id);
        if (existing == null) {
            return false;
        }
        existing.platforms.forEach(stationPlatformRepository::delete);
        return stationRepository.deleteById(id);
    }

    public Optional<Station> getStation(Long id) {
        return getStations().stream()
            .filter(s -> s.getId().equals(id))
            .findFirst();
    }

    private void applyPlatforms(StationEntity stationEntity, List<StationDto.StationPlatformDto> platformDtos) {
        List<StationDto.StationPlatformDto> incoming = platformDtos == null ? List.of() : platformDtos;

        Set<Long> incomingIds = incoming.stream()
            .map(StationDto.StationPlatformDto::id)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

        stationEntity.platforms.stream()
            .filter(platform -> !incomingIds.contains(platform.id))
            .forEach(stationPlatformRepository::delete);

        List<StationPlatformEntity> platforms = new ArrayList<>();
        for (var platformDto : incoming) {
            StationPlatformEntity platformEntity;
            if (platformDto.id() != null) {
                platformEntity = stationPlatformRepository.findById(platformDto.id());
                if (platformEntity == null) {
                    throw new EntityNotFoundException("station platform " + platformDto.id() + " not found");
                }
            } else {
                platformEntity = new StationPlatformEntity();
                platformEntity.station = stationEntity;
            }
            platformEntity.name = platformDto.name();
            platformEntity.trackBlocks = resolveTrackBlocks(platformDto.trackBlockIds());
            stationPlatformRepository.persist(platformEntity);
            platforms.add(platformEntity);
        }
        stationEntity.platforms = platforms;
    }

    private List<TrackBlockEntity> resolveTrackBlocks(List<Long> trackBlockIds) {
        if (trackBlockIds == null) {
            return new ArrayList<>();
        }
        return trackBlockIds.stream()
            .map(id -> trackBlockRepository.findByIdOptional(id)
                .orElseThrow(() -> new EntityNotFoundException("track block " + id + " not found")))
            .collect(Collectors.toList());
    }
}
