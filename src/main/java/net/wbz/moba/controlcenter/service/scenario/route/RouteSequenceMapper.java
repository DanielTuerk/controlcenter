package net.wbz.moba.controlcenter.service.scenario.route;

import jakarta.inject.Inject;
import net.wbz.moba.controlcenter.persist.entity.RouteSequenceEntity;
import net.wbz.moba.controlcenter.shared.scenario.Route;
import net.wbz.moba.controlcenter.shared.scenario.RouteSequence;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * @author Daniel Tuerk
 */
@Mapper(componentModel = "jakarta-cdi")
abstract public class RouteSequenceMapper {

    @Inject
    RouteManager routeManager;

    @Mapping(target = "route", source = "routeId", qualifiedByName = "mapRouteIdToRoute")
    abstract public RouteSequence toDto(RouteSequenceEntity entity);

    @Named("mapRouteIdToRoute")
    public Route mapRouteIdToRoute(Long routeId) {
        if (routeId == null) {
            return null;
        }
        return routeManager.getRouteById(routeId).orElse(null);
    }

}
