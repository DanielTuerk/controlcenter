package net.wbz.moba.controlcenter.web.shared.track.model;

import com.google.common.collect.Lists;
import com.googlecode.jmapper.annotations.JMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Trackpart which is a {@link Straight} in a custom blockLength to display a {@link TrackBlock} with information about the
 * train on the block.
 *
 * @author Daniel Tuerk
 */
public class BlockStraight extends Straight implements MultipleGridPosition {

    @JMap
    private int blockLength;

    @JMap
    private TrackBlock leftTrackBlock;

    @JMap
    private TrackBlock middleTrackBlock;

    @JMap
    private TrackBlock rightTrackBlock;

    @Override
    public Collection<GridPosition> getNextGridPositions(GridPosition previousPosition) {
        int x = getGridPosition().getX();
        int y = getGridPosition().getY();
        int blockLength = getGridLength();
        if (isVertical()) {
            y += blockLength;
        } else {
            x += blockLength;
        }
        return Lists.newArrayList(new GridPosition(x, y));
    }

    @Override
    public GridPosition getEndGridPosition() {
        int x = getGridPosition().getX();
        int y = getGridPosition().getY();
        int blockLength = getGridLength() - 1;
        if (isVertical()) {
            y += blockLength;
        } else {
            x += blockLength;
        }
        return new GridPosition(x, y);
    }

    public Collection<GridPosition> getAllGridPositions() {
        final Set<GridPosition> positions = new HashSet<>();
        GridPosition start = getGridPosition();
        GridPosition end = getEndGridPosition();
        if (getBlockLength() > 1) {
            switch (getDirection()) {
                case HORIZONTAL:
                    IntStream.range(start.getX(), end.getX())
                        .forEach(x -> positions.add(new GridPosition(x, start.getY())));
                    break;
                case VERTICAL:
                    IntStream.range(start.getY(), end.getY())
                        .forEach(y -> positions.add(new GridPosition(start.getX(), y)));
                    break;
            }
            positions.add(end);
        } else {
            return Lists.newArrayList(start, end);
        }
        return positions;
    }


    /**
     * Return the block length which can be set as value or calculated. The length depends on the blocks. Each block
     * will increase the min length by 1.
     *
     * @return length or min 1
     */
    public int getGridLength() {
        int minLength = 0;
        if (getLeftTrackBlock() != null) {
            minLength++;
        }
        if (getMiddleTrackBlock() != null) {
            minLength++;
        }
        if (getRightTrackBlock() != null) {
            minLength++;
        }
        if (minLength == 0) {
            minLength = 1;
        }
        return blockLength < minLength ? minLength : blockLength;
    }

    public void setBlockLength(int blockLength) {
        this.blockLength = blockLength;
    }

    public int getBlockLength() {
        // needed for jmapper
        return blockLength;
    }

    public TrackBlock getLeftTrackBlock() {
        return leftTrackBlock;
    }

    public void setLeftTrackBlock(TrackBlock leftTrackBlock) {
        this.leftTrackBlock = leftTrackBlock;
    }

    public TrackBlock getMiddleTrackBlock() {
        return middleTrackBlock;
    }

    public void setMiddleTrackBlock(TrackBlock middleTrackBlock) {
        this.middleTrackBlock = middleTrackBlock;
    }

    public TrackBlock getRightTrackBlock() {
        return rightTrackBlock;
    }

    public void setRightTrackBlock(TrackBlock rightTrackBlock) {
        this.rightTrackBlock = rightTrackBlock;
    }

    public Collection<TrackBlock> getAllTrackBlocks() {
        return Stream.of(leftTrackBlock, middleTrackBlock, rightTrackBlock).filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "BlockStraight{" + "blockLength=" + blockLength
            + ", leftTrackBlock=" + leftTrackBlock
            + ", middleTrackBlock=" + middleTrackBlock
            + ", rightTrackBlock=" + rightTrackBlock
            + '}'
            + super.toString();
    }
}
