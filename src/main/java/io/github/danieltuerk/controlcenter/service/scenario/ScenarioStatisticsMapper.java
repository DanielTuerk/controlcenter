package io.github.danieltuerk.controlcenter.service.scenario;

import io.github.danieltuerk.controlcenter.persist.entity.ScenarioHistoryEntity;
import io.github.danieltuerk.controlcenter.shared.scenario.ScenarioStatisticRun;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * @author Daniel Tuerk
 */


@Mapper(componentModel = "jakarta-cdi")
public interface ScenarioStatisticsMapper {

    @Mapping(target = "start", source = "startDateTime")
    @Mapping(target = "end", source = "endDateTime")
    @Mapping(target = "averageRunTimeInMillis", source = "elapsedTimeMillis")
    @Mapping(target = "state", source = "type")
    ScenarioStatisticRun toDto(ScenarioHistoryEntity entity);

}
