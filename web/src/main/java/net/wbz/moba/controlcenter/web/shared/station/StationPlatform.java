package net.wbz.moba.controlcenter.web.shared.station;

import com.googlecode.jmapper.annotations.JMap;
import java.util.ArrayList;
import java.util.List;
import net.wbz.moba.controlcenter.web.shared.track.model.AbstractDto;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackBlock;

/**
 * @author Daniel Tuerk
 */
public class StationPlatform extends AbstractDto {

    @JMap
    private String name;
    @JMap
    private List<TrackBlock> trackBlocks;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TrackBlock> getTrackBlocks() {
        if (trackBlocks == null) {
            return new ArrayList<>();
        }
        return trackBlocks;
    }

    public void setTrackBlocks(List<TrackBlock> trackBlocks) {
        this.trackBlocks = trackBlocks;
    }
}
