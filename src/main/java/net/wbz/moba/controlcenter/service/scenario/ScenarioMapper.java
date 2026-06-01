package net.wbz.moba.controlcenter.service.scenario;

import net.wbz.moba.controlcenter.persist.entity.RouteSequenceEntity;
import net.wbz.moba.controlcenter.persist.entity.ScenarioEntity;
import net.wbz.moba.controlcenter.service.scenario.route.RouteSequenceMapper;
import net.wbz.moba.controlcenter.shared.scenario.RouteSequence;
import net.wbz.moba.controlcenter.shared.scenario.Scenario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

/**
 * @author Daniel Tuerk
 */

@Mapper(
    componentModel = MappingConstants.ComponentModel.JAKARTA_CDI,
    uses = RouteSequenceMapper.class
)
public interface ScenarioMapper {

    @Mapping(target = "runState", ignore = true)
    @Mapping(target = "mode", ignore = true)
    Scenario toDto(ScenarioEntity entity);

    List<RouteSequence> map(List<RouteSequenceEntity> entities);
}
