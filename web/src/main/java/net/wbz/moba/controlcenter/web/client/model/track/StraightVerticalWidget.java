package net.wbz.moba.controlcenter.web.client.model.track;

import net.wbz.moba.controlcenter.web.shared.track.model.Straight;

/**
 * @author Daniel Tuerk
 */
public class StraightVerticalWidget extends AbstractStraightWidget {

    @Override
    public AbstractSvgTrackWidget<Straight> getClone() {
        return new StraightVerticalWidget();
    }

    @Override
    public boolean isRepresentationOf(Straight trackPart) {
        return trackPart.getDirection() == Straight.DIRECTION.VERTICAL;
    }

    @Override
    public String getTrackWidgetStyleName() {
        return "widget_track_straight_vertical";
    }

    @Override
    public Straight.DIRECTION getStraightDirection() {
        return Straight.DIRECTION.VERTICAL;
    }
}
