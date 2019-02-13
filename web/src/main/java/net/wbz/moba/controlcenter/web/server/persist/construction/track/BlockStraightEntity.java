package net.wbz.moba.controlcenter.web.server.persist.construction.track;

import com.googlecode.jmapper.annotations.JMap;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import net.wbz.moba.controlcenter.web.shared.track.model.AbstractTrackPart;
import net.wbz.moba.controlcenter.web.shared.track.model.BlockStraight;

/**
 * @author Daniel Tuerk
 */
@Entity
@Table(name = "TRACKPART_BLOCK_STRAIGHT")
public class BlockStraightEntity extends StraightEntity {

    @JMap
    @Column
    private int blockLength;

    @JMap
    @ManyToOne(fetch = FetchType.EAGER)
    private TrackBlockEntity leftTrackBlock;

    @JMap
    @ManyToOne(fetch = FetchType.EAGER)
    private TrackBlockEntity middleTrackBlock;

    @JMap
    @ManyToOne(fetch = FetchType.EAGER)
    private TrackBlockEntity rightTrackBlock;

    public int getBlockLength() {
        return blockLength;
    }

    public void setBlockLength(int blockLength) {
        this.blockLength = blockLength;
    }

    public TrackBlockEntity getLeftTrackBlock() {
        return leftTrackBlock;
    }

    public void setLeftTrackBlock(
        TrackBlockEntity leftTrackBlock) {
        this.leftTrackBlock = leftTrackBlock;
    }

    public TrackBlockEntity getMiddleTrackBlock() {
        return middleTrackBlock;
    }

    public void setMiddleTrackBlock(
        TrackBlockEntity midddleTrackBlock) {
        this.middleTrackBlock = midddleTrackBlock;
    }

    public TrackBlockEntity getRightTrackBlock() {
        return rightTrackBlock;
    }

    public void setRightTrackBlock(
        TrackBlockEntity rightTrackBlock) {
        this.rightTrackBlock = rightTrackBlock;
    }

    @Override
    public Class<? extends AbstractTrackPart> getDefaultDtoClass() {
        return BlockStraight.class;
    }

}
