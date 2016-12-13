package net.wbz.moba.controlcenter.web.client.model.track;

import net.wbz.moba.controlcenter.web.shared.editor.TrackEditorRequest;
import net.wbz.moba.controlcenter.web.shared.track.model.Curve;
import net.wbz.moba.controlcenter.web.shared.track.model.CurveProxy;

/**
 * @author Daniel Tuerk
 */
public class CurveTopLeftWidget extends AbstractCurveWidget {
    @Override
    public AbstractSvgTrackWidget<CurveProxy> getClone() {
        return new CurveTopLeftWidget();
    }

    @Override
    public CurveProxy getNewTrackPart(TrackEditorRequest trackEditorRequest) {
        //TODO
        return null;
//        Curve curveTopLeft = new Curve();
//        curveTopLeft.setDirection(Curve.DIRECTION.TOP_LEFT);
//        return curveTopLeft;
    }


    @Override
    public String getTrackWidgetStyleName() {
        return "widget_track_curve_top_left";
    }



    @Override
    public Curve.DIRECTION getCurveDirection() {
        return Curve.DIRECTION.TOP_LEFT;
    }
}
