package net.wbz.moba.controlcenter.web.client.model.track;

import net.wbz.moba.controlcenter.web.shared.track.model.Curve;
import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGSVGElement;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
public class CurveBottomLeftWidget extends AbstractCurveWidget {

    @Override
    public void initFromTrackPart(Curve trackPart) {
    }

    @Override
    public String getTrackWidgetStyleName() {
        return "widget_track_curve_bottom_left";
    }

    @Override
    public AbstractSvgTrackWidget<Curve> getClone(Curve trackPart) {
        return new CurveBottomLeftWidget();
    }

    @Override
    public Curve.DIRECTION getCurveDirection() {
        return Curve.DIRECTION.BOTTOM_LEFT;
    }
}
