package net.wbz.moba.controlcenter.service.track.block;

import net.wbz.moba.controlcenter.persist.entity.track.TrackBlockEntity;
import net.wbz.moba.controlcenter.shared.track.model.TrackBlock;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * @author Daniel Tuerk
 */
@Mapper(componentModel = "cdi", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TrackBlockMapper {

    TrackBlock toDto(TrackBlockEntity entity);

    TrackBlockEntity toEntity(TrackBlock entity);

}
