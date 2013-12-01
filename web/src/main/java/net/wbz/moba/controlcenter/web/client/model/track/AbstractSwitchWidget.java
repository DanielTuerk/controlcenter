package net.wbz.moba.controlcenter.web.client.model.track;

import com.google.gwt.user.client.ui.Widget;
import net.wbz.moba.controlcenter.web.shared.track.model.Switch;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackPart;

/**
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
abstract public class AbstractSwitchWidget extends AbstractControlImageTrackWidget<Switch> {

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
        if (switchTrackPart.getCurrentDirection() == getDirection() &&
                switchTrackPart.getCurrentPresentation() == getPresentation()) {
            return true;
        }
        return false;
    }

    abstract protected Switch.PRESENTATION getPresentation();

    @Override
    public String getPaletteTitel() {
        return "Switch";
    }


}
