package net.wbz.moba.controlcenter.web.client.model.track;

import net.wbz.moba.controlcenter.web.shared.track.model.Straight;
import net.wbz.moba.controlcenter.web.shared.track.model.Uncoupler;

/**
 * @author Daniel Tuerk
 */
public class UncouplerHorizontalWidget extends AbstractUncouplerWidget {

    @Override
    public Straight.DIRECTION getStraightDirection() {
        return Straight.DIRECTION.HORIZONTAL;
    }

    @Override
    public String getTrackWidgetStyleName() {
        return "widget_track_straight_horizontal";
    }

    @Override
    public AbstractSvgTrackWidget<Uncoupler> getClone() {
        return new UncouplerHorizontalWidget();
    }

    @Override
    public boolean isRepresentationOf(Uncoupler trackPart) {
        return trackPart.getDirection() == Straight.DIRECTION.HORIZONTAL;
    }

}
