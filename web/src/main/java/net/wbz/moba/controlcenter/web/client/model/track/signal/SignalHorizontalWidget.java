package net.wbz.moba.controlcenter.web.client.model.track.signal;

import net.wbz.moba.controlcenter.web.client.RequestUtils;
import net.wbz.moba.controlcenter.web.client.model.track.AbstractSvgTrackWidget;
import net.wbz.moba.controlcenter.web.shared.track.model.SignalProxy;
import net.wbz.moba.controlcenter.web.shared.track.model.Straight;

/**
 * @author Daniel Tuerk
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
    public AbstractSvgTrackWidget<SignalProxy> getClone() {
        return new SignalHorizontalWidget();
    }

    @Override
    public SignalProxy getNewTrackPart() {
        SignalProxy horizontalSignal = RequestUtils.getInstance().getTrackEditorRequest().create(SignalProxy.class);
        horizontalSignal.setDirection(Straight.DIRECTION.HORIZONTAL);
        return horizontalSignal;
    }

    @Override
    public boolean isRepresentationOf(SignalProxy trackPart) {
        return trackPart != null && trackPart.getDirection() == getStraightDirection();
    }

}
