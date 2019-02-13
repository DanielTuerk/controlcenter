package net.wbz.moba.controlcenter.web.client.model.track.block;

/**
 * @author Daniel Tuerk
 */
public interface BlockPart<BlockStraight> {

    void freeBlock();

    void unknownBlock();

    void usedBlock();

    BlockStraight getTrackPart();
}
