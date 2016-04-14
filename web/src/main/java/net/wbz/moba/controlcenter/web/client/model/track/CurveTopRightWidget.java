package net.wbz.moba.controlcenter.web.client.model.track;

import net.wbz.moba.controlcenter.web.shared.track.model.Curve;
import net.wbz.moba.controlcenter.web.shared.track.model.CurveProxy;
import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGSVGElement;

/**
 * @author Daniel Tuerk
 */
public class CurveTopRightWidget extends AbstractCurveWidget {
    @Override
    public AbstractSvgTrackWidget<CurveProxy> getClone() {
        return new CurveTopRightWidget();
    }

    @Override
    public CurveProxy getNewTrackPart() {
        //TODO
        return null;
//        Curve curveTopR = new Curve();
//        curveTopR.setDirection(Curve.DIRECTION.TOP_RIGHT);
//        return curveTopR;
    }

    @Override
    public String getTrackWidgetStyleName() {
        return "widget_track_curve_top_right";
    }

    @Override
    public Curve.DIRECTION getCurveDirection() {
        return Curve.DIRECTION.TOP_RIGHT;
    }
}
