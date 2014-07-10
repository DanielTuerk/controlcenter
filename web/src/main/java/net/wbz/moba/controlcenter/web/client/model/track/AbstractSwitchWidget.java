package net.wbz.moba.controlcenter.web.client.model.track;

import com.google.gwt.user.client.ui.Widget;
import net.wbz.moba.controlcenter.web.shared.track.model.Switch;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackPart;
import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGSVGElement;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
abstract public class AbstractSwitchWidget extends AbstractControlSvgTrackWidget<Switch> {

    @Override
    public TrackPart getTrackPart(Widget containerWidget, int zoomLevel) {
        Switch switchTrackPart = new Switch();
        switchTrackPart.setGridPosition(getGridPosition(containerWidget, zoomLevel));
        switchTrackPart.setCurrentDirection(getDirection());
        switchTrackPart.setCurrentPresentation(getPresentation());
        switchTrackPart.setConfiguration(getStoredWidgetConfiguration());
        return switchTrackPart;
    }

    abstract protected Switch.DIRECTION getDirection();

    @Override
    public boolean isRepresentationOf(Switch switchTrackPart) {
        return switchTrackPart.getCurrentDirection() == getDirection() &&
                switchTrackPart.getCurrentPresentation() == getPresentation();
    }

    abstract protected Switch.PRESENTATION getPresentation();

    @Override
    public String getPaletteTitle() {
        return "Switch";
    }


}
