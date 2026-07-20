package io.github.danieltuerk.controlcenter.service.track;

import io.github.danieltuerk.controlcenter.persist.entity.track.*;
import io.github.danieltuerk.controlcenter.shared.track.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * @author Daniel Tuerk
 */
@Mapper(componentModel = "jakarta-cdi", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TrackPartMapper {

    @Mapping(target = "lastGridPositions", ignore = true)
    Straight toDto(StraightEntity entity);

    @Mapping(target = "allTrackBlocks", ignore = true)
    @Mapping(target = "lastGridPositions", ignore = true)
    BlockStraight toDto(BlockStraightEntity entity);

    Signal toDto(SignalEntity entity);

    Curve toDto(CurveEntity entity);

    Turnout toDto(TurnoutEntity entity);

    Uncoupler toDto(UncouplerEntity entity);

    StraightEntity toEntity(Straight dto);

    BlockStraightEntity toEntity(BlockStraight dto);

    SignalEntity toEntity(Signal dto);

    CurveEntity toEntity(Curve dto);

    TurnoutEntity toEntity(Turnout dto);

    UncouplerEntity toEntity(Uncoupler dto);
}
