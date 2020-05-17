package net.wbz.moba.controlcenter.web.client.model.track;

import net.wbz.moba.controlcenter.web.shared.track.model.Turnout;

/**
 * @author Daniel Tuerk
 */
public class TurnoutRightRightToLeftWidget extends AbstractTurnoutRightWidget {
    @Override
    protected Turnout.PRESENTATION getPresentation() {
        return Turnout.PRESENTATION.RIGHT_TO_LEFT;
    }

    @Override
    public String getTrackWidgetStyleName() {
        return "widget_track_switch_right_right_to_left";
    }

    @Override
    public AbstractSvgTrackWidget<Turnout> getClone() {
        return new TurnoutRightRightToLeftWidget();
    }

    @Override
    public Turnout getNewTrackPart() {
        Turnout turnoutRRightL = new Turnout();
        turnoutRRightL.setCurrentDirection(Turnout.DIRECTION.RIGHT);
        turnoutRRightL.setCurrentPresentation(Turnout.PRESENTATION.RIGHT_TO_LEFT);
        return turnoutRRightL;
    }

}
