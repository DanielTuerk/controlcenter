package net.wbz.moba.controlcenter.service.scenario;

import net.wbz.moba.controlcenter.persist.entity.RouteEntity;
import net.wbz.moba.controlcenter.shared.scenario.Route;
import org.mapstruct.Mapper;

/**
 * @author Daniel Tuerk
 */
@Mapper(componentModel = "cdi")
public interface RouteMapper {

    Route toDto(RouteEntity entity);

}
