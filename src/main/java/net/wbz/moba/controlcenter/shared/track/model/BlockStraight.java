package net.wbz.moba.controlcenter.shared.track.model;

import lombok.Getter;
import lombok.Setter;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Trackpart which is a {@link Straight} in a custom blockLength to display a {@link TrackBlock} with information about the
 * train on the block.
 *
 * @author Daniel Tuerk
 */
@Setter
@Getter
@Schema(
    description = "type for a track part",
    allOf = {Straight.class}
)
@Tag(ref = "track")
public class BlockStraight extends Straight implements MultipleGridPosition {

    private int blockLength;

    private TrackBlock leftTrackBlock;

    private TrackBlock middleTrackBlock;

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
        return List.of(new GridPosition(x, y));
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
        return Math.max(blockLength, minLength);
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
