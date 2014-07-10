package net.wbz.moba.controlcenter.web.client.model.track;

import net.wbz.moba.controlcenter.web.shared.track.model.Switch;
import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGSVGElement;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
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
    public AbstractSvgTrackWidget<Switch> getClone(Switch trackPart) {
        SwitchLeftBottomToTopWidget clone = new SwitchLeftBottomToTopWidget();
        clone.initFromTrackPart(trackPart);
        return clone;
    }

}
