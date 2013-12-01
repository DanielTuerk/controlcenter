package net.wbz.moba.controlcenter.web.client.model.track;

import net.wbz.moba.controlcenter.web.shared.track.model.Signal;
import net.wbz.moba.controlcenter.web.shared.track.model.Straight;

/**
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
public class SignalVerticalWidget extends AbstractSignalWidget {

    @Override
    public Straight.DIRECTION getStraightDirection() {
        return Straight.DIRECTION.VERTICAL;
    }

    @Override
    public String getTrackWidgetStyleName() {
        return "widget_track_straight_vertical";
    }

    @Override
    public AbstractImageTrackWidget<Signal> getClone(Signal trackPart) {
        SignalVerticalWidget clone = new SignalVerticalWidget();
        clone.initFromTrackPart(trackPart);
        return clone;
    }


    @Override
    public boolean isRepresentationOf(Signal trackPart) {
        if (trackPart instanceof Signal) {
            return trackPart.getDirection() == Straight.DIRECTION.HORIZONTAL;
        }
        return false;
    }

}
