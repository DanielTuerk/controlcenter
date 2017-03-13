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

    @ManyToOne
    private StationEntity station;

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
