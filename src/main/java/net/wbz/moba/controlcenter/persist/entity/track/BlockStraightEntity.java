package net.wbz.moba.controlcenter.persist.entity.track;


import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * @author Daniel Tuerk
 */
@Entity
@Table(name = "TRACKPART_BLOCK_STRAIGHT")
public class BlockStraightEntity extends StraightEntity {

    public int blockLength;

    @ManyToOne(fetch = FetchType.EAGER)
    public TrackBlockEntity leftTrackBlock;

    @ManyToOne(fetch = FetchType.EAGER)
    public TrackBlockEntity middleTrackBlock;

    @ManyToOne(fetch = FetchType.EAGER)
    public TrackBlockEntity rightTrackBlock;

}
