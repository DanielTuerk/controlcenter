package io.github.danieltuerk.controlcenter.service.config;

import io.github.danieltuerk.controlcenter.api.config.ConfigItem;
import io.github.danieltuerk.controlcenter.persist.entity.ConfigValueEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * @author Daniel Tuerk
 */
@Mapper(componentModel = "jakarta-cdi", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ConfigMapper {

    ConfigItem toDto(ConfigValueEntity entity);
}
