package net.wbz.moba.controlcenter.service.train;

import net.wbz.moba.controlcenter.persist.entity.TrainEntity;
import net.wbz.moba.controlcenter.shared.train.Train;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * @author Daniel Tuerk
 */
@Mapper(componentModel = "cdi", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TrainMapper {

    Train toDto(TrainEntity entity);

}
