package io.github.danieltuerk.controlcenter.service.scenario.route;

import io.github.danieltuerk.controlcenter.persist.entity.RouteSequenceEntity;
import io.github.danieltuerk.controlcenter.shared.scenario.Route;
import io.github.danieltuerk.controlcenter.shared.scenario.RouteSequence;
import jakarta.inject.Inject;
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
