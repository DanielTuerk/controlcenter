package io.github.danieltuerk.controlcenter.service.track;

import io.github.danieltuerk.controlcenter.persist.entity.track.BusDataConfigurationEntity;
import io.github.danieltuerk.controlcenter.shared.track.model.BusDataConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * @author Daniel Tuerk
 */
@Mapper(componentModel = "jakarta-cdi", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BusDataConfigMapper {

    BusDataConfiguration toDto(BusDataConfigurationEntity entity);

    BusDataConfigurationEntity toEntity(BusDataConfiguration dto);

}
