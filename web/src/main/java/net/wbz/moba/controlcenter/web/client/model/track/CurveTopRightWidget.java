package net.wbz.moba.controlcenter.web.client.model.track;

import net.wbz.moba.controlcenter.web.shared.track.model.Curve;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
public class CurveTopRightWidget extends AbstractCurveWidget {

    @Override
    public AbstractImageTrackWidget<Curve> getClone(Curve trackPart) {
        return new CurveTopRightWidget();
    }

    @Override
    public void initFromTrackPart(Curve trackPart) {
        //To change body of implemented methods use File | Settings | File Templates.
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
