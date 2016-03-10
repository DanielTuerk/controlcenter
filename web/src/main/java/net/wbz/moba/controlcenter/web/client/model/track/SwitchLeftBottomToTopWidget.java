package net.wbz.moba.controlcenter.web.client.model.track;

import net.wbz.moba.controlcenter.web.shared.track.model.Switch;
import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGSVGElement;

/**
 * @author Daniel Tuerk
 */
public class SwitchLeftBottomToTopWidget extends AbstractSwitchLeftWidget {
    @Override
    protected Switch.PRESENTATION getPresentation() {
        return Switch.PRESENTATION.BOTTOM_TO_TOP;
    }

    @Override
    public String getTrackWidgetStyleName() {
        return "widget_track_switch_left_bottom_to_top";
    }

    @Override
    public AbstractSvgTrackWidget<Switch> getClone() {
        return new SwitchLeftBottomToTopWidget();
    }

    @Override
    public Switch getNewTrackPart() {
        Switch switchBottomTop = new Switch();
        switchBottomTop.setCurrentDirection(Switch.DIRECTION.LEFT);
        switchBottomTop.setCurrentPresentation(Switch.PRESENTATION.BOTTOM_TO_TOP);
        return switchBottomTop;
    }

}
