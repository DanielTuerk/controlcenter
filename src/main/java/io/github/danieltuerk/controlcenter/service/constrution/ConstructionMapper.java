package io.github.danieltuerk.controlcenter.service.constrution;

import io.github.danieltuerk.controlcenter.persist.entity.ConstructionEntity;
import io.github.danieltuerk.controlcenter.shared.constrution.Construction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * @author Daniel Tuerk
 */
@Mapper(componentModel = "jakarta-cdi")
public interface ConstructionMapper {

    @Mapping(target = "inAutomaticMode", ignore = true)
    Construction toDto(ConstructionEntity entity);

}
