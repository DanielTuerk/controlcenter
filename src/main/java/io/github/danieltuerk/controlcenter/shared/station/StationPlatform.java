package io.github.danieltuerk.controlcenter.shared.station;


import io.github.danieltuerk.controlcenter.shared.track.model.AbstractDto;
import io.github.danieltuerk.controlcenter.shared.track.model.BlockStraight;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Daniel Tuerk
 */
public class StationPlatform extends AbstractDto {

    private String name;
    private List<BlockStraight> blockStraights;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<BlockStraight> getBlockStraights() {
        if (blockStraights == null) {
            return new ArrayList<>();
        }
        return blockStraights;
    }

    public void setBlockStraights(List<BlockStraight> blockStraights) {
        this.blockStraights = blockStraights;
    }
}
