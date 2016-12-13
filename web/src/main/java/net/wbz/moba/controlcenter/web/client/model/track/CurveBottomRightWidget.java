package net.wbz.moba.controlcenter.web.client.model.track;

import net.wbz.moba.controlcenter.web.client.RequestUtils;
import net.wbz.moba.controlcenter.web.shared.editor.TrackEditorRequest;
import net.wbz.moba.controlcenter.web.shared.track.model.Curve;
import net.wbz.moba.controlcenter.web.shared.track.model.CurveProxy;

/**
 * @author Daniel Tuerk
 */
public class CurveBottomRightWidget extends AbstractCurveWidget {
    @Override
    public AbstractSvgTrackWidget<CurveProxy> getClone() {
        return new CurveBottomRightWidget();
    }

    @Override
    public CurveProxy getNewTrackPart(TrackEditorRequest trackEditorRequest) {
        CurveProxy curveBottomR = RequestUtils.getInstance().getTrackEditorRequest().create(CurveProxy.class);
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
