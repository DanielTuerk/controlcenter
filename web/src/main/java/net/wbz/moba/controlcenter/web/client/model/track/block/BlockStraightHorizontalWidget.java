package net.wbz.moba.controlcenter.web.client.model.track.block;

import net.wbz.moba.controlcenter.web.client.model.track.AbstractSvgTrackWidget;
import net.wbz.moba.controlcenter.web.shared.track.model.BlockStraight;
import net.wbz.moba.controlcenter.web.shared.track.model.Straight.DIRECTION;

/**
 * @author Daniel Tuerk
 */
public class BlockStraightHorizontalWidget extends AbstractBlockStraightWidget {

    @Override
    protected BlockStraightEditBlockFieldSet createBlockFieldSet() {
        return new BlockStraightEditBlockFieldSet("Left", "Right", getTrackPart());
    }

    @Override
    public DIRECTION getStraightDirection() {
        return DIRECTION.HORIZONTAL;
    }

    @Override
    protected AbstractSvgTrackWidget<BlockStraight> getClone() {
        return new BlockStraightHorizontalWidget();
    }

    @Override
    protected int getWidgetWidth() {
        int blockLength = getTrackPart().getGridLength();
        return AbstractSvgTrackWidget.WIDGET_WIDTH * (blockLength > 0 ? blockLength : 1);
    }
}
