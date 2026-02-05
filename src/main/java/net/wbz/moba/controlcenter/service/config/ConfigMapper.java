package net.wbz.moba.controlcenter.service.config;

import net.wbz.moba.controlcenter.api.config.ConfigItem;
import net.wbz.moba.controlcenter.persist.entity.ConfigValueEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * @author Daniel Tuerk
 */
@Mapper(componentModel = "cdi", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ConfigMapper {

    ConfigItem toDto(ConfigValueEntity entity);
}
