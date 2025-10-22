package net.wbz.moba.controlcenter.service.scenario;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import net.wbz.moba.controlcenter.EventBroadcaster;
import net.wbz.moba.controlcenter.persist.entity.RouteEntity;
import net.wbz.moba.controlcenter.persist.repository.RouteRepository;
import net.wbz.moba.controlcenter.persist.repository.RouteSequenceRepository;
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

    private final RouteRepository routeDao;
    private final RouteSequenceRepository routeSequenceDao;
    private final EventBroadcaster eventBroadcaster;
    private final RouteMapper routeMapper;
    private final TrackBuilder trackBuilder;

    private final List<RoutesChangedListener> listeners = new ArrayList<>();

    @Inject
    public RouteManager(RouteRepository routeRepository, RouteSequenceRepository routeSequenceRepository,
        EventBroadcaster eventBroadcaster,
        RouteMapper routeMapper, TrackBuilder trackBuilder, ConstructionService constructionService) {
        this.routeDao = routeRepository;
        this.routeSequenceDao = routeSequenceRepository;
        this.eventBroadcaster = eventBroadcaster;
        this.routeMapper = routeMapper;
        this.trackBuilder = trackBuilder;

        // load routes initial to build all tracks
        constructionService.addListener(x -> loadRoutesFromDatabase());
    }

    public void addListener(RoutesChangedListener listener) {
        listeners.add(listener);
    }

    public void removeListener(RoutesChangedListener listener) {
        listeners.remove(listener);
    }

    synchronized Collection<Route> getRoutes() {
        if (routes.isEmpty()) {
            loadRoutesFromDatabase();
        }
        return routes;
    }

    @Transactional
    void updateRoute(Route route) {
        // TODO migrate
//        RouteEntity entity = routeMapper.transformTarget(route);
//        routeDao.update(entity);
//        loadRoutesFromDatabase();
//        fireRoutesChanged();
    }

    @Transactional
    void createRoute(Route route) {
        final var entity = new RouteEntity();
        entity.name = route.getName();
        // TODO migrate
//        entity.start=route.getStart()
        routeDao.persist(entity);
        loadRoutesFromDatabase();
        fireRoutesChanged(entity.id);
    }

    /**
     * Delete the {@link Route} for the given id and reload the cached data.
     *
     * @param routeId id of {@link Route} to delete
     */
    @Transactional
    void deleteRoute(long routeId) {
        // TODO migrate
        if (!routeSequenceDao.routeUsedInScenario(routeId)) {
            routeDao.deleteById(routeId);
            loadRoutesFromDatabase();
            fireRoutesChanged(routeId);
        } else {
            LOG.error("can't delete route, still in use of scenario");
        }
    }

    Optional<Route> getRouteById(Long id) {
        return routes.stream().filter(x -> id.equals(x.getId())).findFirst();
    }

    private void loadRoutesFromDatabase() {
        LOG.debug("load routes from database");

        routes.clear();
        routes.addAll(routeDao.listAll().stream().map(routeMapper::toDto).toList());
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
}
