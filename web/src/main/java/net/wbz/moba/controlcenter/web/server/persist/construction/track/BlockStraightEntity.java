package net.wbz.moba.controlcenter.web.server.persist.construction.track;

import com.googlecode.jmapper.annotations.JMap;
import javax.persistence.Column;
import javax.persistence.Entity;
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

    public int getBlockLength() {
        return blockLength;
    }

    public void setBlockLength(int blockLength) {
        this.blockLength = blockLength;
    }

    @Override
    public Class<? extends AbstractTrackPart> getDefaultDtoClass() {
        return BlockStraight.class;
    }

}
