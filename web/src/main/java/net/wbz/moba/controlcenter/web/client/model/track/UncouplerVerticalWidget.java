package net.wbz.moba.controlcenter.web.client.model.track;

import net.wbz.moba.controlcenter.web.shared.track.model.Straight;
import net.wbz.moba.controlcenter.web.shared.track.model.Uncoupler;

/**
 * @author Daniel Tuerk
 */
public class UncouplerVerticalWidget extends AbstractUncouplerWidget {

    @Override
    public AbstractSvgTrackWidget<Uncoupler> getClone() {
        return new UncouplerVerticalWidget();
    }

    @Override
    public boolean isRepresentationOf(Uncoupler trackPart) {
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
