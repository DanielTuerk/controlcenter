package net.wbz.moba.controlcenter.web.server.persist.scenario;

import com.googlecode.jmapper.annotations.JMap;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import net.wbz.moba.controlcenter.web.server.persist.AbstractEntity;
import net.wbz.moba.controlcenter.web.server.persist.construction.track.TrackBlockEntity;

/**
 * @author Daniel Tuerk
 */
@Entity(name = "STATION_PLATFORM")
public class StationPlatformEntity extends AbstractEntity {

    @JMap
    @Column
    private String name;

    @JMap
    @OneToMany(fetch = FetchType.EAGER)
    private List<TrackBlockEntity> trackBlocks;

    @ManyToOne
    private StationEntity station;

    public String getName() {
        return name;
    }

    public void setName(String platformName) {
        this.name = platformName;
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
