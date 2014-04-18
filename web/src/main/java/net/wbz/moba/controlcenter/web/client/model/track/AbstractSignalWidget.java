package net.wbz.moba.controlcenter.web.client.model.track;

import com.google.gwt.user.client.ui.Widget;
import net.wbz.moba.controlcenter.web.shared.track.model.Signal;
import net.wbz.moba.controlcenter.web.shared.track.model.Straight;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackPart;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
abstract public class AbstractSignalWidget extends AbstractControlImageTrackWidget<Signal> {

    @Override
    public String getImageUrl() {
        return "img/widget/track/straight_signal_stop.png";
    }

    @Override
    public String getActiveStateImageUrl() {
        return "img/widget/track/straight_signal_drive.png";
    }

    @Override
    public TrackPart getTrackPart(Widget containerWidget, int zoomLevel) {
        Signal signal = new Signal();
        signal.setDirection(getStraightDirection());
        signal.setGridPosition(getGridPosition(containerWidget, zoomLevel));
        signal.setConfiguration(getStoredWidgetConfiguration());
        return signal;
    }

    abstract public Straight.DIRECTION getStraightDirection();

    @Override
    public String getPaletteTitel() {
        return "Signal";
    }

}
