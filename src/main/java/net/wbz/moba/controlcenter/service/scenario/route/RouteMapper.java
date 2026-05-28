package net.wbz.moba.controlcenter.service.scenario.route;

import net.wbz.moba.controlcenter.persist.entity.RouteEntity;
import net.wbz.moba.controlcenter.shared.scenario.Route;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


/**
 * @author Daniel Tuerk
 */
@Mapper(componentModel = "cdi", uses = {RouteSequenceMapper.class})
public interface RouteMapper {

    @Mapping(target = "track", ignore = true)
    Route toDto(RouteEntity entity);

}
