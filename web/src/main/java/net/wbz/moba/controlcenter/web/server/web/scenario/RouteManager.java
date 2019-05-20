package net.wbz.moba.controlcenter.web.server.web.scenario;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import net.wbz.moba.controlcenter.web.server.event.EventBroadcaster;
import net.wbz.moba.controlcenter.web.server.persist.scenario.RouteDao;
import net.wbz.moba.controlcenter.web.server.persist.scenario.RouteDataMapper;
import net.wbz.moba.controlcenter.web.server.persist.scenario.RouteEntity;
import net.wbz.moba.controlcenter.web.server.persist.scenario.RouteSequenceDao;
import net.wbz.moba.controlcenter.web.server.persist.scenario.TrackBuilder;
import net.wbz.moba.controlcenter.web.server.web.constrution.ConstructionServiceImpl;
import net.wbz.moba.controlcenter.web.shared.scenario.Route;
import net.wbz.moba.controlcenter.web.shared.scenario.RoutesChangedEvent;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario;
import net.wbz.moba.controlcenter.web.shared.scenario.TrackNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manager to access the {@link Scenario}s from database. The data is cached. TODO cache Stations
 *
 * @author Daniel Tuerk
 */
@Singleton
public class RouteManager {

    private static final Logger LOG = LoggerFactory.getLogger(RouteManager.class);

    /**
     * Cached routes from persistence.
     */
    private final List<Route> routes = new ArrayList<>();

    private final RouteDao routeDao;
    private final RouteSequenceDao routeSequenceDao;
    private final EventBroadcaster eventBroadcaster;
    private final RouteDataMapper routeDataMapper;
    private final TrackBuilder trackBuilder;

    private final List<RoutesChangedListener> listeners = new ArrayList<>();

    @Inject
    public RouteManager(RouteDao routeDao, RouteSequenceDao routeSequenceDao, EventBroadcaster eventBroadcaster,
        RouteDataMapper routeDataMapper, TrackBuilder trackBuilder, ConstructionServiceImpl constructionService) {
        this.routeDao = routeDao;
        this.routeSequenceDao = routeSequenceDao;
        this.eventBroadcaster = eventBroadcaster;
        this.routeDataMapper = routeDataMapper;
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
        RouteEntity entity = routeDataMapper.transformTarget(route);
        routeDao.update(entity);
        loadRoutesFromDatabase();
        fireRoutesChanged();
    }

    @Transactional
    void createRoute(Route route) {
        RouteEntity entity = routeDataMapper.transformTarget(route);
        routeDao.create(entity);
        loadRoutesFromDatabase();
        fireRoutesChanged();
    }

    /**
     * Delete the {@link Route} for the given id and reload the cached data.
     *
     * @param routeId id of {@link Route} to delete
     */
    @Transactional
    void deleteRoute(long routeId) {
        if (!routeSequenceDao.routeUsedInScenario(routeId)) {
            routeSequenceDao.delete(routeId);
            loadRoutesFromDatabase();
            fireRoutesChanged();
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
        routes.addAll(routeDataMapper.transformSource(routeDao.listAll()));
        LOG.debug("build tracks");

        for (Route route : routes) {
            try {
                route.setTrack(trackBuilder.build(route));
            } catch (TrackNotFoundException e) {
                LOG.error("can't build track of route: {} ({})", new Object[]{route, e.getMessage()});
            }
        }
        LOG.debug("tracks finished");
    }

    private void fireRoutesChanged() {
        listeners.forEach(RoutesChangedListener::routesChanged);

        // TODO split to create, update, delete event; to prevent reload and rebuild all tracks
        eventBroadcaster.fireEvent(new RoutesChangedEvent());
    }
}
