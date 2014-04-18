package net.wbz.moba.controlcenter.web.client.model.track;

import net.wbz.moba.controlcenter.web.shared.track.model.Straight;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
public class StraightVerticalWidget extends AbstractStraightWidget {

    @Override
    public String getTrackWidgetStyleName() {
        return "widget_track_straight_vertical";
    }

    @Override
    public AbstractImageTrackWidget<Straight> getClone(Straight trackPart) {
        return new StraightVerticalWidget();
    }

    @Override
    public void initFromTrackPart(Straight trackPart) {
    }

    @Override
    public boolean isRepresentationOf(Straight trackPart) {
        if (trackPart instanceof Straight) {
            return trackPart.getDirection() == Straight.DIRECTION.VERTICAL;
        }
        return false;
    }

    @Override
    public Straight.DIRECTION getStraightDirection() {
        return Straight.DIRECTION.VERTICAL;
    }
}
