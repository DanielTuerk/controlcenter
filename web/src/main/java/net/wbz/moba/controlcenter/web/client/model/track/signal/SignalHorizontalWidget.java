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
    public AbstractSvgTrackWidget<Signal> getClone() {
        return new SignalHorizontalWidget();
    }

    @Override
    public Signal getNewTrackPart() {
        Signal horizontalSignal = new Signal();
        horizontalSignal.setDirection(Straight.DIRECTION.HORIZONTAL);
        return horizontalSignal;
    }

    @Override
    public boolean isRepresentationOf(Signal trackPart) {
        return trackPart != null && trackPart.getDirection() == getStraightDirection();
    }

}
