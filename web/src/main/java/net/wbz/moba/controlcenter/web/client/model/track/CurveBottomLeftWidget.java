package net.wbz.moba.controlcenter.web.client.model.track;

import net.wbz.moba.controlcenter.web.shared.editor.TrackEditorRequest;
import net.wbz.moba.controlcenter.web.shared.track.model.Curve;
import net.wbz.moba.controlcenter.web.shared.track.model.CurveProxy;

/**
 * @author Daniel Tuerk
 */
public class CurveBottomLeftWidget extends AbstractCurveWidget {


    @Override
    public String getTrackWidgetStyleName() {
        return "widget_track_curve_bottom_left";
    }

    @Override
    public AbstractSvgTrackWidget<CurveProxy> getClone() {
        return new CurveBottomLeftWidget();
    }

    @Override
    public CurveProxy getNewTrackPart(TrackEditorRequest trackEditorRequest) {
        //TODO
        return null;
//        Curve curveBottomL = new Curve();
//        curveBottomL.setDirection(Curve.DIRECTION.BOTTOM_LEFT);
//        return curveBottomL;
    }

    @Override
    public Curve.DIRECTION getCurveDirection() {
        return Curve.DIRECTION.BOTTOM_LEFT;
    }
}
