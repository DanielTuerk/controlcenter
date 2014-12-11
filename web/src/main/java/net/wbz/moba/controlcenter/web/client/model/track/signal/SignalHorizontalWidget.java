package net.wbz.moba.controlcenter.web.client.model.track.signal;

import net.wbz.moba.controlcenter.web.client.model.track.AbstractSvgTrackWidget;
import net.wbz.moba.controlcenter.web.shared.track.model.Signal;
import net.wbz.moba.controlcenter.web.shared.track.model.Straight;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
public class SignalHorizontalWidget extends AbstractSignalWidget {

    @Override
    public Straight.DIRECTION getStraightDirection() {
        return Straight.DIRECTION.HORIZONTAL;
    }

    @Override
    public String getTrackWidgetStyleName() {
        return "widget_track_straight_horizontal";
    }

    @Override
    public AbstractSvgTrackWidget<Signal> getClone(Signal trackPart) {
        SignalHorizontalWidget clone = new SignalHorizontalWidget();
        clone.initFromTrackPart(trackPart);
        return clone;
    }

    @Override
    public boolean isRepresentationOf(Signal trackPart) {
        return trackPart != null && trackPart.getDirection() == getStraightDirection();
    }

}
