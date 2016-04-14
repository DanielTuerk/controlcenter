package net.wbz.moba.controlcenter.web.client.model.track;

import net.wbz.moba.controlcenter.web.client.util.SvgTrackUtil;
import net.wbz.moba.controlcenter.web.shared.track.model.Straight;
import net.wbz.moba.controlcenter.web.shared.track.model.StraightProxy;
import org.vectomatic.dom.svg.OMSVGRectElement;

/**
 * @author Daniel Tuerk
 */
public class StraightVerticalWidget extends AbstractStraightWidget {

    @Override
    public AbstractSvgTrackWidget<StraightProxy> getClone() {
        return new StraightVerticalWidget();
    }

    @Override
    public boolean isRepresentationOf(StraightProxy trackPart) {
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
