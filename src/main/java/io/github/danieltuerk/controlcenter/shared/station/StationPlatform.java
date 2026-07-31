package io.github.danieltuerk.controlcenter.shared.station;


import io.github.danieltuerk.controlcenter.shared.track.model.AbstractDto;
import io.github.danieltuerk.controlcenter.shared.track.model.BlockStraight;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Daniel Tuerk
 */
@Setter
public class StationPlatform extends AbstractDto {

    @Getter
    private String name;
    private List<BlockStraight> blockStraights;

    public List<BlockStraight> getBlockStraights() {
        if (blockStraights == null) {
            return new ArrayList<>();
        }
        return blockStraights;
    }

}
