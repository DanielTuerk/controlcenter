package net.wbz.moba.controlcenter.service.track;

import net.wbz.moba.controlcenter.persist.entity.track.BlockStraightEntity;
import net.wbz.moba.controlcenter.persist.entity.track.CurveEntity;
import net.wbz.moba.controlcenter.persist.entity.track.SignalEntity;
import net.wbz.moba.controlcenter.persist.entity.track.StraightEntity;
import net.wbz.moba.controlcenter.persist.entity.track.TurnoutEntity;
import net.wbz.moba.controlcenter.persist.entity.track.UncouplerEntity;
import net.wbz.moba.controlcenter.shared.track.model.BlockStraight;
import net.wbz.moba.controlcenter.shared.track.model.Curve;
import net.wbz.moba.controlcenter.shared.track.model.Signal;
import net.wbz.moba.controlcenter.shared.track.model.Straight;
import net.wbz.moba.controlcenter.shared.track.model.Turnout;
import net.wbz.moba.controlcenter.shared.track.model.Uncoupler;
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
