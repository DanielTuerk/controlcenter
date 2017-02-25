package net.wbz.moba.controlcenter.web.server.persist.scenario;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import com.googlecode.jmapper.annotations.JMap;

import net.wbz.moba.controlcenter.web.server.persist.AbstractEntity;

/**
 * @author Daniel Tuerk
 */
@Entity(name = "STATION_RAIL")
public class StationRailEntity extends AbstractEntity {

    @JMap
    @Column
    private int railNumber;

    @JMap
    @ManyToOne
    private StationEntity station;

    // TODO geht auch über die route blocks - oder direkt zugriff erlauben?
    // @ManyToOne
    // private SignalEntity frontSignal;
    // @ManyToOne
    // private TrackBlockEntity frontBreakingBlock;
    // @ManyToOne
    // private TrackBlockEntity frontStopBlock;
    //
    // @ManyToOne
    // private SignalEntity backSignal;
    // @ManyToOne
    // private TrackBlockEntity backBreakingBlock;
    // @ManyToOne
    // private TrackBlockEntity backStopBlock;

    public int getRailNumber() {
        return railNumber;
    }

    public void setRailNumber(int railNumber) {
        this.railNumber = railNumber;
    }

    public StationEntity getStation() {
        return station;
    }

    public void setStation(StationEntity station) {
        this.station = station;
    }
}
