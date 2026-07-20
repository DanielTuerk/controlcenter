package io.github.danieltuerk.controlcenter.service.track.block;

import io.github.danieltuerk.controlcenter.persist.entity.track.TrackBlockEntity;
import io.github.danieltuerk.controlcenter.shared.track.model.TrackBlock;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * @author Daniel Tuerk
 */
@Mapper(componentModel = "jakarta-cdi", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TrackBlockMapper {

    TrackBlock toDto(TrackBlockEntity entity);

}
