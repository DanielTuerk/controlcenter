package net.wbz.moba.controlcenter.web.client.model.track.signal;

import net.wbz.moba.controlcenter.web.client.RequestUtils;
import net.wbz.moba.controlcenter.web.client.model.track.AbstractSvgTrackWidget;
import net.wbz.moba.controlcenter.web.shared.editor.TrackEditorRequest;
import net.wbz.moba.controlcenter.web.shared.track.model.Signal;
import net.wbz.moba.controlcenter.web.shared.track.model.SignalProxy;
import net.wbz.moba.controlcenter.web.shared.track.model.Straight;

/**
 * @author Daniel Tuerk
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
    public AbstractSvgTrackWidget<SignalProxy> getClone() {
        return new SignalVerticalWidget();
    }

    @Override
    public SignalProxy getNewTrackPart(TrackEditorRequest trackEditorRequest) {
        SignalProxy verticalSignal = RequestUtils.getInstance().getTrackEditorRequest().create(SignalProxy.class);
        verticalSignal.setDirection(Straight.DIRECTION.VERTICAL);
        verticalSignal.setType(Signal.TYPE.BLOCK);
        return verticalSignal;
    }

    @Override
    public boolean isRepresentationOf(SignalProxy trackPart) {
        return trackPart != null && trackPart.getDirection() == getStraightDirection();
    }
}
