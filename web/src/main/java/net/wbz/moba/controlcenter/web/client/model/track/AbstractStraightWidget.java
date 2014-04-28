package net.wbz.moba.controlcenter.web.client.model.track;

import com.google.gwt.user.client.ui.Widget;
import net.wbz.moba.controlcenter.web.shared.track.model.Straight;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackPart;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
abstract public class AbstractStraightWidget extends AbstractBlockImageTrackWidget<Straight> {

    @Override
    public String getImageUrl() {
        return "img/widget/track/straight.png";
    }

    @Override
    public TrackPart getTrackPart(Widget containerWidget, int zoomLevel) {
        Straight straight = new Straight();
        straight.setDirection(getStraightDirection());
        straight.setGridPosition(getGridPosition(containerWidget, zoomLevel));
        straight.setConfiguration(getStoredWidgetConfiguration());
        return straight;
    }

    abstract public Straight.DIRECTION getStraightDirection();

    @Override
    public String getPaletteTitel() {
        return "Straight";
    }



}
