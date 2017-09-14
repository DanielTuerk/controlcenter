package net.wbz.moba.controlcenter.web.shared.scenario;

import com.googlecode.jmapper.annotations.JMap;

import com.googlecode.jmapper.annotations.JMapConversion;
import com.googlecode.jmapper.annotations.JMapConversion.Type;
import java.util.List;
import javax.persistence.ManyToOne;
import net.wbz.moba.controlcenter.web.shared.track.model.AbstractDto;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackBlock;

/**
 * @author Daniel Tuerk
 */
public class StationRail extends AbstractDto {

    @JMap
    private int railNumber;
    @JMap
    private List<TrackBlock> trackBlocks;

    public int getRailNumber() {
        return railNumber;
    }

    public void setRailNumber(int railNumber) {
        this.railNumber = railNumber;
    }

    public List<TrackBlock> getTrackBlocks() {
        return trackBlocks;
    }

    public void setTrackBlocks(List<TrackBlock> trackBlocks) {
        this.trackBlocks = trackBlocks;
    }
}
