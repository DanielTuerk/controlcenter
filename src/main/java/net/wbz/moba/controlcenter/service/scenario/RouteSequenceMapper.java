package net.wbz.moba.controlcenter.service.scenario;

import net.wbz.moba.controlcenter.persist.entity.RouteSequenceEntity;
import net.wbz.moba.controlcenter.shared.scenario.RouteSequence;
import org.mapstruct.Mapper;

/**
 * @author Daniel Tuerk
 */
@Mapper(componentModel = "cdi")
public interface RouteSequenceMapper {

    RouteSequence toDto(RouteSequenceEntity entity);

}
