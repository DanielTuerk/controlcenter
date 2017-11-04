package net.wbz.moba.controlcenter.web.client.model.track;

import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGSVGElement;

import net.wbz.moba.controlcenter.web.client.util.SvgTrackUtil;
import net.wbz.moba.controlcenter.web.shared.track.model.Straight;

/**
 * @author Daniel Tuerk
 */
abstract public class AbstractStraightWidget extends AbstractBlockSvgTrackWidget<Straight> {

    @Override
    protected void addSvgContent(OMSVGDocument doc, OMSVGSVGElement svg, String color) {
        svg.appendChild(SvgTrackUtil.createRectangle(getSvgDocument(), 0f, 10f, 25f, 5f, color));
    }

    @Override
    public Straight getNewTrackPart() {
        Straight straight = new Straight();
        straight.setDirection(getStraightDirection());
        return straight;
    }

    abstract public Straight.DIRECTION getStraightDirection();

    @Override
    public String getPaletteTitle() {
        return "Straight";
    }

}
