package net.wbz.moba.controlcenter.web.client.model.track;

import net.wbz.moba.controlcenter.web.shared.track.model.Curve;
import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGSVGElement;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
public class CurveBottomRightWidget extends AbstractCurveWidget {
    @Override
    public AbstractSvgTrackWidget<Curve> getClone(Curve trackPart) {
        return new CurveBottomRightWidget();
    }

    @Override
    public void initFromTrackPart(Curve trackPart) {
        //To change body of implemented methods use File | Settings | File Templates.
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
