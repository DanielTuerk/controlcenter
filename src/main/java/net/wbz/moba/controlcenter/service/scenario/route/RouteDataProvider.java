package net.wbz.moba.controlcenter.service.scenario.route;

import io.quarkus.cache.CacheInvalidateAll;
import io.quarkus.cache.CacheResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import net.wbz.moba.controlcenter.persist.entity.RouteEntity;
import net.wbz.moba.controlcenter.persist.entity.track.BlockStraightEntity;
import net.wbz.moba.controlcenter.persist.repository.RouteRepository;
import net.wbz.moba.controlcenter.persist.repository.RouteSequenceRepository;
import net.wbz.moba.controlcenter.persist.repository.track.GridPositionRepository;
import net.wbz.moba.controlcenter.persist.repository.track.TrackBlockRepository;
import net.wbz.moba.controlcenter.persist.repository.track.TrackPartRepository;
import net.wbz.moba.controlcenter.shared.constrution.CurrentConstructionChangeEvent;
import net.wbz.moba.controlcenter.shared.scenario.Route;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class RouteDataProvider {

    private static final Logger LOG = Logger.getLogger(RouteDataProvider.class);
    private static final String CACHE = "routes-cache";

    private final RouteSequenceRepository routeSequenceDao;
    private final RouteRepository routeRepository;
    private final TrackBlockRepository trackBlockRepository;
    private final TrackPartRepository trackPartRepository;
    private final GridPositionRepository gridPositionRepository;

    @Inject
    public RouteDataProvider(RouteSequenceRepository routeSequenceDao, RouteRepository routeRepository,
                             TrackBlockRepository trackBlockRepository, TrackPartRepository trackPartRepository,
                             GridPositionRepository gridPositionRepository) {
        this.routeSequenceDao = routeSequenceDao;
        this.routeRepository = routeRepository;
        this.trackBlockRepository = trackBlockRepository;
        this.trackPartRepository = trackPartRepository;
        this.gridPositionRepository = gridPositionRepository;
    }

    @CacheInvalidateAll(cacheName = CACHE)
    public void onCurrentConstructionChanged(@Observes CurrentConstructionChangeEvent event) {
        LOG.infof("current construction changed for ID: %s, refreshing routes...", event.construction().getId());
    }

    @CacheResult(cacheName = CACHE)
    public synchronized List<RouteEntity> getRoutes() {
        return routeRepository.listAll();
    }

    @CacheInvalidateAll(cacheName = CACHE)
    @Transactional
    public RouteEntity createRoute(Route route) {
        final var toCreate = new RouteEntity();
        fillEntityFromRoute(route, toCreate);

        routeRepository.persist(toCreate);
        return toCreate;
    }

    @CacheInvalidateAll(cacheName = CACHE)
    @Transactional
    public RouteEntity updateRoute(Long id, Route route) {
        final var toUpdate = routeRepository.findById(id);

        fillEntityFromRoute(route, toUpdate);

        routeRepository.persist(toUpdate);
        return toUpdate;
    }

    @CacheInvalidateAll(cacheName = CACHE)
    @Transactional
    public boolean deleteRoute(long routeId) {
        if (!routeSequenceDao.routeUsedInScenario(routeId)) {
            routeRepository.deleteById(routeId);
            return true;
        } else {
            LOG.error("can't delete route, still in use of scenario");
            return false;
        }
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
