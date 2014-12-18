package net.wbz.moba.controlcenter.web.client.model.track;

import net.wbz.moba.controlcenter.web.shared.track.model.Curve;
import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGSVGElement;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
public class CurveBottomRightWidget extends AbstractCurveWidget {
    @Override
    public AbstractSvgTrackWidget<Curve> getClone() {
        return new CurveBottomRightWidget();
    }

    @Override
    public Curve getNewTrackPart() {
        Curve curveBottomR = new Curve();
        curveBottomR.setDirection(Curve.DIRECTION.BOTTOM_RIGHT);
        return curveBottomR;
    }

    @Override
    public String getTrackWidgetStyleName() {
        return "widget_track_curve_bottom_right";
    }

    @Override
    public Curve.DIRECTION getCurveDirection() {
        return Curve.DIRECTION.BOTTOM_RIGHT;
    }
}
