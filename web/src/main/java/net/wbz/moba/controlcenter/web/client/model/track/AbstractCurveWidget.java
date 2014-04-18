package net.wbz.moba.controlcenter.web.client.model.track;

import com.google.gwt.user.client.ui.Widget;
import net.wbz.moba.controlcenter.web.shared.track.model.Curve;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackPart;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
abstract public class AbstractCurveWidget extends AbstractImageTrackWidget<Curve> {

    @Override
    public String getImageUrl() {
        return "img/widget/track/curve.png";
    }

    @Override
    public TrackPart getTrackPart(Widget containerWidget, int zoomLevel) {
        Curve curve = new Curve();
        curve.setDirection(getCurveDirection());
        curve.setGridPosition(getGridPosition(containerWidget, zoomLevel));
        return curve;
    }

    @Override
    public boolean isRepresentationOf(Curve trackPart) {
        return trackPart.getDirection() == getCurveDirection();
    }

    abstract public Curve.DIRECTION getCurveDirection();

    @Override
    public String getPaletteTitel() {
        return "Curve";
    }
}
