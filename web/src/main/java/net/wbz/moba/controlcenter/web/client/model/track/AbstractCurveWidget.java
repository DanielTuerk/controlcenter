package net.wbz.moba.controlcenter.web.client.model.track;

import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGSVGElement;

import net.wbz.moba.controlcenter.web.client.util.SvgTrackUtil;
import net.wbz.moba.controlcenter.web.shared.track.model.Curve;

/**
 * @author Daniel Tuerk
 */
abstract public class AbstractCurveWidget extends AbstractSvgTrackWidget<Curve> {

    @Override
    protected void addSvgContent(OMSVGDocument doc, OMSVGSVGElement svg, String color) {
        svg.appendChild(SvgTrackUtil.createLine(doc, 12f, 26f, 26f, 12f, 5, color));
    }

    @Override
    public boolean isRepresentationOf(Curve trackPart) {
        return trackPart.getDirection() == getCurveDirection();
    }

    abstract public Curve.DIRECTION getCurveDirection();

    @Override
    public String getPaletteTitle() {
        return "Curve";
    }
}
