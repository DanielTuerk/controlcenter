package net.wbz.moba.controlcenter.web.client.model.track.block;

import net.wbz.moba.controlcenter.web.client.model.track.AbstractSvgTrackWidget;
import net.wbz.moba.controlcenter.web.client.util.SvgTrackUtil;
import net.wbz.moba.controlcenter.web.shared.track.model.BlockStraight;
import net.wbz.moba.controlcenter.web.shared.track.model.Straight.DIRECTION;
import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGRectElement;
import org.vectomatic.dom.svg.OMSVGSVGElement;

/**
 * Vertical presentation for {@link AbstractBlockStraightWidget}. It will create own SVG.
 *
 * @author Daniel Tuerk
 */
public class BlockStraightVerticalWidget extends AbstractBlockStraightWidget {

    @Override
    protected BlockStraightEditBlockFieldSet createBlockFieldSet() {
        return new BlockStraightEditBlockFieldSet("Top", "Bottom", getTrackPart());
    }

    @Override
    public DIRECTION getStraightDirection() {
        return DIRECTION.VERTICAL;
    }

    @Override
    protected AbstractSvgTrackWidget<BlockStraight> getClone() {
        return new BlockStraightVerticalWidget();
    }

    @Override
    protected int getWidgetHeight() {
        int blockLength = getTrackPart().getGridLength();
        return AbstractSvgTrackWidget.WIDGET_HEIGHT * (blockLength > 0 ? blockLength : 1);
    }


    @Override
    protected void addSvgContent(OMSVGDocument doc, OMSVGSVGElement svg, String color) {
        int height = getWidgetHeight();

        svg.appendChild(SvgTrackUtil.createRectangle(getSvgDocument(), 10f, 0f, 5f, height, color));

        OMSVGRectElement lastPaintBlockSvg = SvgTrackUtil
            .createRectangle(getSvgDocument(), 2f, BOX_PADDING, 21f, height - 2 * BOX_PADDING, color);
        svg.appendChild(lastPaintBlockSvg);

        setLastPaintBlockSvg(lastPaintBlockSvg);
    }
}
