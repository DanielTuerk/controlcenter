package net.wbz.moba.controlcenter.web.client.model.track;

import net.wbz.moba.controlcenter.web.shared.track.model.BlockStraight;
import net.wbz.moba.controlcenter.web.shared.track.model.Straight.DIRECTION;

/**
 * @author Daniel Tuerk
 */
public class BlockStraightHorizontalWidget extends AbstractBlockStraightWidget {

    @Override
    public DIRECTION getStraightDirection() {
        return DIRECTION.HORIZONTAL;
    }

    @Override
    public String getTrackWidgetStyleName() {
        return "widget_track_block_straight_horizontal";
    }

    @Override
    protected AbstractSvgTrackWidget<BlockStraight> getClone() {
        return new BlockStraightHorizontalWidget();
    }
}
