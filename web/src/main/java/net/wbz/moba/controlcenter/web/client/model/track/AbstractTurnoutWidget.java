package net.wbz.moba.controlcenter.web.client.model.track;

import net.wbz.moba.controlcenter.web.shared.track.model.Turnout;

/**
 * @author Daniel Tuerk
 */
abstract public class AbstractTurnoutWidget extends AbstractControlSvgTrackWidget<Turnout> {

    abstract protected Turnout.DIRECTION getDirection();

    @Override
    public boolean isRepresentationOf(Turnout turnoutTrackPart) {
        return turnoutTrackPart.getCurrentDirection() == getDirection() &&
                turnoutTrackPart.getCurrentPresentation() == getPresentation();
    }

    abstract protected Turnout.PRESENTATION getPresentation();

    @Override
    public String getPaletteTitle() {
        return "Turnout";
    }

}
