package net.wbz.moba.controlcenter.web.client.model.track;

import net.wbz.moba.controlcenter.web.shared.track.model.Switch;

/**
 * @author Daniel Tuerk
 */
abstract public class AbstractSwitchWidget extends AbstractControlSvgTrackWidget<Switch> {

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
