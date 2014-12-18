package net.wbz.moba.controlcenter.web.client.model.track;

import net.wbz.moba.controlcenter.web.client.util.SvgTrackUtil;
import net.wbz.moba.controlcenter.web.shared.track.model.Straight;
import org.vectomatic.dom.svg.OMSVGRectElement;

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
    public AbstractSvgTrackWidget<Straight> getClone() {
        StraightHorizontalWidget widget = new StraightHorizontalWidget();
        return widget;
    }

    @Override
    public Straight getNewTrackPart() {
        Straight straight = new Straight();
        straight.setDirection(Straight.DIRECTION.VERTICAL);
        return straight;
    }

    @Override
    public boolean isRepresentationOf(Straight trackPart) {
        return trackPart.getDirection() == Straight.DIRECTION.HORIZONTAL;
    }

}
