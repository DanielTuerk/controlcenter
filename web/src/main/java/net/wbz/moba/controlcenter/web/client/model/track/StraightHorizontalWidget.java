package net.wbz.moba.controlcenter.web.client.model.track;

import net.wbz.moba.controlcenter.web.shared.track.model.Straight;

/**
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
public class StraightHorizontalWidget extends AbstractStraightWidget {

    @Override
    public Straight.DIRECTION getStraightDirection() {
        return Straight.DIRECTION.HORIZONTAL;
    }

    @Override
    public String getTrackWidgetStyleName() {
        return "widget_track_straight_horizontal";
    }

    @Override
    public AbstractImageTrackWidget<Straight> getClone(Straight trackPart) {
        return new StraightHorizontalWidget();
    }

    @Override
    public void initFromTrackPart(Straight trackPart) {
    }

    @Override
    public boolean isRepresentationOf(Straight trackPart) {
        if (trackPart instanceof Straight) {
            return trackPart.getDirection() == Straight.DIRECTION.HORIZONTAL;
        }
        return false;
    }

}
