package io.github.danieltuerk.controlcenter.shared.station;


import io.github.danieltuerk.controlcenter.shared.track.model.AbstractDto;
import io.github.danieltuerk.controlcenter.shared.track.model.TrackBlock;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Daniel Tuerk
 */
public class StationPlatform extends AbstractDto {

    private String name;
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
