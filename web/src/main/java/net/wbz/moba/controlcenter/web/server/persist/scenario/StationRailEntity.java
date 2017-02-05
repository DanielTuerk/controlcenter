package net.wbz.moba.controlcenter.web.server.persist.scenario;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import net.wbz.moba.controlcenter.web.server.persist.AbstractEntity;
import net.wbz.moba.controlcenter.web.server.persist.construction.track.SignalEntity;
import net.wbz.moba.controlcenter.web.server.persist.construction.track.TrackBlockEntity;

/**
 *
 * @author Daniel Tuerk
 */
@Entity(name = "STATION_RAIL")
public class StationRailEntity extends AbstractEntity {

    @Column
    private int railNumber;

//    @ManyToOne
//    private SignalEntity frontSignal;
//    @ManyToOne
//    private TrackBlockEntity frontBreakingBlock;
//    @ManyToOne
//    private TrackBlockEntity frontStopBlock;
//
//    @ManyToOne
//    private SignalEntity backSignal;
//    @ManyToOne
//    private TrackBlockEntity backBreakingBlock;
//    @ManyToOne
//    private TrackBlockEntity backStopBlock;

}
