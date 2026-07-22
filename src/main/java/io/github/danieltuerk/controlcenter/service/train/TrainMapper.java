package io.github.danieltuerk.controlcenter.service.train;

import io.github.danieltuerk.controlcenter.persist.entity.TrainEntity;
import io.github.danieltuerk.controlcenter.shared.train.Train;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * @author Daniel Tuerk
 */
@Mapper(componentModel = "jakarta-cdi", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TrainMapper {

    Train toDto(TrainEntity entity);

}
