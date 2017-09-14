package net.wbz.moba.controlcenter.web.server.persist.scenario;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.googlecode.jmapper.annotations.JMap;

import net.wbz.moba.controlcenter.web.server.persist.AbstractEntity;
import net.wbz.moba.controlcenter.web.server.persist.construction.track.TrackBlockEntity;
import net.wbz.moba.controlcenter.web.shared.scenario.Station;

/**
 * @author Daniel Tuerk
 */
@Entity(name = "STATION_RAIL")
public class StationRailEntity extends AbstractEntity {

    @JMap
    @Column
    private int railNumber;

    @JMap
    @OneToMany
    private List<TrackBlockEntity> trackBlocks;

    @ManyToOne
    private StationEntity station;

    public int getRailNumber() {
        return railNumber;
    }

    public void setRailNumber(int railNumber) {
        this.railNumber = railNumber;
    }

    public List<TrackBlockEntity> getTrackBlocks() {
        return trackBlocks;
    }

    public void setTrackBlocks(List<TrackBlockEntity> trackBlocks) {
        this.trackBlocks = trackBlocks;
    }

    public StationEntity getStation() {
        return station;
    }

    public void setStation(StationEntity station) {
        this.station = station;
    }
}
