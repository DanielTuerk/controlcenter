package io.github.danieltuerk.controlcenter.service.scenario;

import io.github.danieltuerk.controlcenter.persist.entity.RouteSequenceEntity;
import io.github.danieltuerk.controlcenter.persist.entity.ScenarioEntity;
import io.github.danieltuerk.controlcenter.service.scenario.route.RouteSequenceMapper;
import io.github.danieltuerk.controlcenter.shared.scenario.RouteSequence;
import io.github.danieltuerk.controlcenter.shared.scenario.Scenario;
import org.mapstruct.Mapper;
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

    Scenario toDto(ScenarioEntity entity);

    List<RouteSequence> map(List<RouteSequenceEntity> entities);
}
