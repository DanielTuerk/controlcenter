package io.github.danieltuerk.controlcenter.service.station;

import io.github.danieltuerk.controlcenter.persist.entity.StationEntity;
import io.github.danieltuerk.controlcenter.persist.entity.StationPlatformEntity;
import io.github.danieltuerk.controlcenter.service.track.TrackPartMapper;
import io.github.danieltuerk.controlcenter.shared.station.Station;
import io.github.danieltuerk.controlcenter.shared.station.StationPlatform;
import org.mapstruct.Mapper;

/**
 * @author Daniel Tuerk
 */
@Mapper(componentModel = "jakarta-cdi", uses = TrackPartMapper.class)
public interface StationMapper {

    Station toDto(StationEntity entity);

    StationPlatform toDto(StationPlatformEntity entity);

}
