package net.wbz.moba.controlcenter.service.scenario.route;

import io.quarkus.cache.CacheInvalidateAll;
import io.quarkus.cache.CacheResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import net.wbz.moba.controlcenter.EventBroadcaster;
import net.wbz.moba.controlcenter.persist.entity.ConstructionEntity;
import net.wbz.moba.controlcenter.persist.entity.RouteEntity;
import net.wbz.moba.controlcenter.persist.entity.track.BlockStraightEntity;
import net.wbz.moba.controlcenter.persist.repository.ConstructionRepository;
import net.wbz.moba.controlcenter.persist.repository.RouteRepository;
import net.wbz.moba.controlcenter.persist.repository.RouteSequenceRepository;
import net.wbz.moba.controlcenter.persist.repository.track.GridPositionRepository;
import net.wbz.moba.controlcenter.persist.repository.track.TrackBlockRepository;
import net.wbz.moba.controlcenter.persist.repository.track.TrackPartRepository;
import net.wbz.moba.controlcenter.service.constrution.ConstructionService;
import net.wbz.moba.controlcenter.service.scenario.TrackBuilder;
import net.wbz.moba.controlcenter.shared.constrution.Construction;
import net.wbz.moba.controlcenter.shared.constrution.CurrentConstructionChangeEvent;
import net.wbz.moba.controlcenter.shared.scenario.Route;
import net.wbz.moba.controlcenter.shared.scenario.RoutesChangedEvent;
import net.wbz.moba.controlcenter.shared.scenario.TrackNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ApplicationScoped
public class RouteDataProvider {

    private static final String CACHE = "routes-cache";

    private final RouteSequenceRepository routeSequenceDao;
    private final RouteRepository routeRepository;
    private final TrackBlockRepository trackBlockRepository;
    private final TrackPartRepository trackPartRepository;
    private final GridPositionRepository gridPositionRepository;
    private final ConstructionService constructionService;
    private final ConstructionRepository constructionRepository;
    private final RouteMapper routeMapper;
    private final TrackBuilder trackBuilder;
    private final EventBroadcaster eventBroadcaster;

    @Inject
    public RouteDataProvider(RouteSequenceRepository routeSequenceDao, RouteRepository routeRepository,
                             TrackBlockRepository trackBlockRepository, TrackPartRepository trackPartRepository,
                             GridPositionRepository gridPositionRepository, ConstructionService constructionService,
                             ConstructionRepository constructionRepository, RouteMapper routeMapper, TrackBuilder trackBuilder, EventBroadcaster eventBroadcaster) {
        this.routeSequenceDao = routeSequenceDao;
        this.routeRepository = routeRepository;
        this.trackBlockRepository = trackBlockRepository;
        this.trackPartRepository = trackPartRepository;
        this.gridPositionRepository = gridPositionRepository;
        this.constructionService = constructionService;
        this.constructionRepository = constructionRepository;
        this.routeMapper = routeMapper;
        this.trackBuilder = trackBuilder;
        this.eventBroadcaster = eventBroadcaster;
    }

    @CacheInvalidateAll(cacheName = CACHE)
    public void onCurrentConstructionChanged(@Observes CurrentConstructionChangeEvent event) {
        log.info("current construction changed for ID: {}, refreshing routes...", event.construction().getId());
        eventBroadcaster.fireEvent(new RoutesChangedEvent(false));
    }

    @CacheResult(cacheName = CACHE)
    public List<Route> getRoutes() {
        return constructionService.getCurrentConstruction()
            .map(construction -> routeRepository.listAll(construction.getId()))
            .orElse(List.of())
            .stream().map(routeMapper::toDto)
            .peek(route -> {
                try {
                    route.setTrack(trackBuilder.build(route));
                } catch (TrackNotFoundException e) {
                    log.error("can't build track of route: {} ({})", route, e.getMessage());
                }
            })
            .collect(Collectors.toList());
    }

    @CacheInvalidateAll(cacheName = CACHE)
    @Transactional
    public Long createRoute(Route route) {
        final var toCreate = new RouteEntity();
        toCreate.construction = currentConstructionEntity();
        fillEntityFromRoute(route, toCreate);

        routeRepository.persist(toCreate);
        return toCreate.id;
    }

    @CacheInvalidateAll(cacheName = CACHE)
    @Transactional
    public void updateRoute(Long id, Route route) {
        final var toUpdate = routeRepository.findById(id);

        fillEntityFromRoute(route, toUpdate);

        routeRepository.persist(toUpdate);
    }

    @CacheInvalidateAll(cacheName = CACHE)
    @Transactional
    public boolean deleteRoute(long routeId) {
        if (!routeSequenceDao.routeUsedInScenario(routeId)) {
            routeRepository.deleteById(routeId);
            return true;
        } else {
            log.error("can't delete route, still in use of scenario");
            return false;
        }
    }

    private ConstructionEntity currentConstructionEntity() {
        return constructionRepository.findById(
            constructionService.getCurrentConstruction()
                .map(Construction::getId)
                .orElse(null));
    }

    private void fillEntityFromRoute(Route route, RouteEntity toUpdate) {
        toUpdate.name = route.getName();
        toUpdate.start = route.getStart() == null ? null
            : (BlockStraightEntity) trackPartRepository.findById(route.getStart().getId());
        toUpdate.end = route.getEnd() == null ? null
            : trackBlockRepository.findById(route.getEnd().getId());
        toUpdate.oneway = route.getOneway();

        if (toUpdate.waypoints == null) {
            toUpdate.waypoints = new ArrayList<>();
        }
        toUpdate.waypoints.clear();
        if (route.getWaypoints() != null) {
            toUpdate.waypoints.addAll(route.getWaypoints().stream()
                .map(gridPositionRepository::findByGridPosition)
                .collect(Collectors.toSet()));
        }
    }
}
