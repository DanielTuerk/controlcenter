package net.wbz.moba.controlcenter.service.track;

import net.wbz.moba.controlcenter.persist.entity.track.BusDataConfigurationEntity;
import net.wbz.moba.controlcenter.shared.track.model.BusDataConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * @author Daniel Tuerk
 */
@Mapper(componentModel = "cdi", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BusDataConfigMapper {

    BusDataConfiguration toDto(BusDataConfigurationEntity entity);

    BusDataConfigurationEntity toEntity(BusDataConfiguration dto);

}
