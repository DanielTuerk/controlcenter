package net.wbz.moba.controlcenter.web.client.model.track;

import com.google.gwt.user.client.ui.Widget;
import net.wbz.moba.controlcenter.web.shared.track.model.Straight;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackPart;

/**
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
abstract public class AbstractStraightWidget extends AbstractImageTrackWidget<Straight> {

    @Override
    public String getImageUrl() {
        return "img/widget/track/straight.png";
    }

    @Override
    public TrackPart getTrackPart(Widget containerWidget, int zoomLevel) {
        Straight curve = new Straight();
        curve.setDirection(getStraightDirection());
        curve.setGridPosition(getGridPosition(containerWidget, zoomLevel));
        return curve;
    }

    abstract public Straight.DIRECTION getStraightDirection();

    @Override
    public String getPaletteTitel() {
        return "Straight";
    }



}
