package net.wbz.moba.controlcenter.service.scenario;

import net.wbz.moba.controlcenter.persist.entity.ConstructionEntity;
import net.wbz.moba.controlcenter.persist.entity.ScenarioEntity;
import net.wbz.moba.controlcenter.shared.constrution.Construction;
import net.wbz.moba.controlcenter.shared.scenario.Scenario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * @author Daniel Tuerk
 */
@Mapper(componentModel = "cdi")
public interface ScenarioMapper {

    @Mapping(target = "runState", ignore = true)
    @Mapping(target = "mode", ignore = true)
    Scenario toDto(ScenarioEntity entity);

}
