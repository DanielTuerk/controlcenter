package net.wbz.moba.controlcenter.service.scenario;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.wbz.moba.controlcenter.EventBroadcaster;
import net.wbz.moba.controlcenter.persist.entity.RouteEntity;
import net.wbz.moba.controlcenter.persist.entity.track.BlockStraightEntity;
import net.wbz.moba.controlcenter.persist.repository.RouteRepository;
import net.wbz.moba.controlcenter.persist.repository.RouteSequenceRepository;
import net.wbz.moba.controlcenter.persist.repository.track.GridPositionRepository;
import net.wbz.moba.controlcenter.persist.repository.track.TrackBlockRepository;
import net.wbz.moba.controlcenter.persist.repository.track.TrackPartRepository;
import net.wbz.moba.controlcenter.service.constrution.ConstructionService;
import net.wbz.moba.controlcenter.shared.scenario.Route;
import net.wbz.moba.controlcenter.shared.scenario.RoutesChangedEvent;
import net.wbz.moba.controlcenter.shared.scenario.Scenario;
import net.wbz.moba.controlcenter.shared.scenario.TrackNotFoundException;
import org.jboss.logging.Logger;

/**
 * Manager to access the {@link Scenario}s from database. The data is cached. TODO cache Stations
 *
 * @author Daniel Tuerk
 */
@ApplicationScoped
public class RouteManager {

    private static final Logger LOG = Logger.getLogger(RouteManager.class);

    /**
     * Cached routes from persistence.
     */
    private final List<Route> routes = new ArrayList<>();

    private final RouteSequenceRepository routeSequenceDao;
    private final EventBroadcaster eventBroadcaster;
    private final RouteMapper routeMapper;
    private final TrackBuilder trackBuilder;

    private final List<RoutesChangedListener> listeners = new ArrayList<>();
    private final RouteRepository routeRepository;
    private final TrackBlockRepository trackBlockRepository;
    private final TrackPartRepository trackPartRepository;
    private final GridPositionRepository gridPositionRepository;

    @Inject
    public RouteManager(RouteRepository routeRepository, RouteSequenceRepository routeSequenceRepository,
        EventBroadcaster eventBroadcaster,
        RouteMapper routeMapper, TrackBuilder trackBuilder, ConstructionService constructionService,
        TrackBlockRepository trackBlockRepository, TrackPartRepository trackPartRepository,
        GridPositionRepository gridPositionRepository) {
        this.routeSequenceDao = routeSequenceRepository;
        this.eventBroadcaster = eventBroadcaster;
        this.routeMapper = routeMapper;
        this.trackBuilder = trackBuilder;
        this.trackBlockRepository = trackBlockRepository;
        this.trackPartRepository = trackPartRepository;
        this.gridPositionRepository = gridPositionRepository;

        // load routes initial to build all tracks
        constructionService.addListener(x -> loadRoutesFromDatabase());
        this.routeRepository = routeRepository;
    }

    public void addListener(RoutesChangedListener listener) {
        listeners.add(listener);
    }

    public void removeListener(RoutesChangedListener listener) {
        listeners.remove(listener);
    }

    public synchronized List<Route> getRoutes() {
        if (routes.isEmpty()) {
            loadRoutesFromDatabase();
        }
        return routes;
    }

    @Transactional
    public void updateRoute(Long id, Route route) {
        final var toUpdate = routeRepository.findById(id);

        fillEntityFromRoute(route, toUpdate);

        routeRepository.persist(toUpdate);
        loadRoutesFromDatabase();
        fireRoutesChanged(id);
    }

    @Transactional
    public Route createRoute(Route route) {
        final var toCreate = new RouteEntity();
        fillEntityFromRoute(route, toCreate);

        routeRepository.persist(toCreate);

        loadRoutesFromDatabase();
        fireRoutesChanged(toCreate.id);

        return routeMapper.toDto(toCreate);
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

    /**
     * Delete the {@link Route} for the given id and reload the cached data.
     *
     * @param routeId id of {@link Route} to delete
     */
    @Transactional
    public boolean deleteRoute(long routeId) {
        if (!routeSequenceDao.routeUsedInScenario(routeId)) {
            routeRepository.deleteById(routeId);
            loadRoutesFromDatabase();
            fireRoutesChanged(routeId);
            return true;
        } else {
            LOG.error("can't delete route, still in use of scenario");
            return false;
        }
    }

    public Optional<Route> getRouteById(Long id) {
        return routes.stream().filter(x -> id.equals(x.getId())).findFirst();
    }

    private void loadRoutesFromDatabase() {
        LOG.debug("load routes from database");

        routes.clear();
        routes.addAll(routeRepository.listAll().stream().map(routeMapper::toDto).toList());
        LOG.debug("build tracks");

        for (Route route : routes) {
            try {
                route.setTrack(trackBuilder.build(route));
            } catch (TrackNotFoundException e) {
                LOG.error("can't build track of route: %s (%s)".formatted(route, e.getMessage()));
            }
        }
        LOG.debug("tracks finished");
    }

    private void fireRoutesChanged(Long id) {
        listeners.forEach(RoutesChangedListener::routesChanged);

        // TODO split to create, update, delete event; to prevent reload and rebuild all tracks
        eventBroadcaster.fireEvent(new RoutesChangedEvent(id));
    }

    public boolean existsById(Long id) {
        return getRouteById(id).isPresent();
    }
}
