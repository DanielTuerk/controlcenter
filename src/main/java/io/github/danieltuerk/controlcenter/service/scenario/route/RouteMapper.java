package io.github.danieltuerk.controlcenter.service.scenario.route;

import io.github.danieltuerk.controlcenter.persist.entity.RouteEntity;
import io.github.danieltuerk.controlcenter.service.track.TrackPartMapper;
import io.github.danieltuerk.controlcenter.shared.scenario.Route;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


/**
 * @author Daniel Tuerk
 */
@Mapper(componentModel = "jakarta-cdi", uses = {RouteSequenceMapper.class, TrackPartMapper.class})
public interface RouteMapper {

    @Mapping(target = "track", ignore = true)
    @Mapping(target = "allTrackBlocksToDrive", ignore = true)
    Route toDto(RouteEntity entity);

}
