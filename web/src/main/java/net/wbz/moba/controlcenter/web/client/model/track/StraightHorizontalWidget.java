package net.wbz.moba.controlcenter.web.client.model.track;

import net.wbz.moba.controlcenter.web.shared.track.model.Straight;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
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
        StraightHorizontalWidget widget = new StraightHorizontalWidget();
        widget.initFromTrackPart(trackPart);
        return widget;
    }

    @Override
    public boolean isRepresentationOf(Straight trackPart) {
        return trackPart.getDirection() == Straight.DIRECTION.HORIZONTAL;
    }

}
