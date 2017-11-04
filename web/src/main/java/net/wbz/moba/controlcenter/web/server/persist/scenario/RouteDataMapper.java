package net.wbz.moba.controlcenter.web.server.persist.scenario;

import net.wbz.moba.controlcenter.web.server.persist.scenario.RouteEntity;
import net.wbz.moba.controlcenter.web.server.web.DataMapper;
import net.wbz.moba.controlcenter.web.shared.scenario.Route;

/**
 * @author Daniel Tuerk
 */
public class RouteDataMapper extends DataMapper<Route, RouteEntity> {

    /**
     * Create new mapper.
     */
    public RouteDataMapper() {
        super(Route.class, RouteEntity.class);
    }

}
