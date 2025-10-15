package net.wbz.moba.controlcenter.model;

import net.wbz.moba.controlcenter.persist.entity.ConstructionEntity;
import net.wbz.moba.controlcenter.shared.constrution.Construction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * @author Daniel Tuerk
 */
@Mapper(componentModel = "cdi")
public interface ConstructionMapper {

    @Mapping(target = "inAutomaticMode", ignore = true)
    Construction toDto(ConstructionEntity entity);
    ConstructionEntity toEntity(Construction dto);

}
