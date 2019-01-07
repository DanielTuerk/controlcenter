package net.wbz.moba.controlcenter.web.shared.track.model;

import com.google.common.collect.Lists;
import com.googlecode.jmapper.annotations.JMap;
import java.util.Collection;

/**
 * Trackpart which is a {@link Straight} in a custom blockLength to display a {@link TrackBlock} with information about the
 * train on the block.
 *
 * @author Daniel Tuerk
 */
public class BlockStraight extends Straight {

    @JMap
    private int blockLength;

    @Override
    public Collection<GridPosition> getNextGridPositions(GridPosition previousPosition) {
        int x = getGridPosition().getX();
        int y = getGridPosition().getY();
        if (isVertical()) {
            y += blockLength;
        } else {
            x += blockLength;
        }
        return Lists.newArrayList(new GridPosition(x, y));
    }

    @Override
    public Collection<GridPosition> getLastGridPositions() {
        int x = getGridPosition().getX();
        int y = getGridPosition().getY();
        if (isVertical()) {
            y -= blockLength;
        } else {
            x -= blockLength;
        }
        return Lists.newArrayList(new GridPosition(x, y));
    }

    public int getBlockLength() {
        return blockLength;
    }

    public void setBlockLength(int blockLength) {
        this.blockLength = blockLength;
    }
}
