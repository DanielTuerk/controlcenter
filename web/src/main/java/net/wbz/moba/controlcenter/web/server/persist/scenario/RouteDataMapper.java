package net.wbz.moba.controlcenter.web.server.persist.scenario;


import java.util.List;
import java.util.stream.Collectors;
import net.wbz.moba.controlcenter.web.server.persist.construction.track.GridPositionDao;
import net.wbz.moba.controlcenter.web.server.persist.construction.track.GridPositionEntity;
import net.wbz.moba.controlcenter.web.server.web.DataMapper;
import net.wbz.moba.controlcenter.shared.scenario.Route;

/**
 * {@link DataMapper} for the {@link Route}.
 *
 * @author Daniel Tuerk
 */
public class RouteDataMapper extends DataMapper<Route, RouteEntity> {

    private final GridPositionDao gridPositionDao;

    /**
     * Create new mapper.
     *
     * @param gridPositionDao {@link GridPositionDao}
     */
    @Inject
    public RouteDataMapper(GridPositionDao gridPositionDao) {
        super(Route.class, RouteEntity.class);
        this.gridPositionDao = gridPositionDao;
    }

    @Override
    public RouteEntity transformTarget(Route route) {
        RouteEntity routeEntity = super.transformTarget(route);
        routeEntity.setWaypoints(fetchGridPositions(routeEntity));
        return routeEntity;
    }

    /**
     * Fetch the entity from database because the route is manipulated on client side and can't simply transform to
     * source. The id of the {@link GridPositionEntity} would always be null and will create duplicates by persistence.
     *
     * @param entity {@link RouteEntity}
     * @return list of fetched {@link GridPositionEntity}s from persistence context
     */
    private List<GridPositionEntity> fetchGridPositions(RouteEntity entity) {
        return entity.getWaypoints().stream().map(gridPositionDao::findByGridPositionCopy).collect(Collectors.toList());
    }
}
