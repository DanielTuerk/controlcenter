package net.wbz.moba.controlcenter.web.client.model.track;

import com.google.gwt.user.client.ui.Widget;
import net.wbz.moba.controlcenter.web.shared.track.model.Switch;
import net.wbz.moba.controlcenter.web.shared.track.model.SwitchProxy;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackPart;

/**
 * @author Daniel Tuerk
 */
abstract public class AbstractSwitchWidget extends AbstractControlSvgTrackWidget<SwitchProxy> {

    abstract protected Switch.DIRECTION getDirection();

    @Override
    public boolean isRepresentationOf(SwitchProxy switchTrackPart) {
        return switchTrackPart.getCurrentDirection() == getDirection() &&
                switchTrackPart.getCurrentPresentation() == getPresentation();
    }

    abstract protected Switch.PRESENTATION getPresentation();

    @Override
    public String getPaletteTitle() {
        return "Switch";
    }


}
