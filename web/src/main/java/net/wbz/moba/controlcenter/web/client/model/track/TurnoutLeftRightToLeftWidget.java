package net.wbz.moba.controlcenter.web.client.model.track;

import net.wbz.moba.controlcenter.web.shared.track.model.Turnout;

/**
 * @author Daniel Tuerk
 */
public class TurnoutLeftRightToLeftWidget extends AbstractTurnoutLeftWidget {
    @Override
    protected Turnout.PRESENTATION getPresentation() {
        return Turnout.PRESENTATION.RIGHT_TO_LEFT;
    }

    @Override
    public String getTrackWidgetStyleName() {
        return "widget_track_switch_left_right_to_left";
    }

    @Override
    public AbstractSvgTrackWidget<Turnout> getClone() {
        return new TurnoutLeftRightToLeftWidget();
    }

    @Override
    public Turnout getNewTrackPart() {
        Turnout turnoutRightL = new Turnout();
        turnoutRightL.setCurrentDirection(Turnout.DIRECTION.LEFT);
        turnoutRightL.setCurrentPresentation(Turnout.PRESENTATION.RIGHT_TO_LEFT);
        return turnoutRightL;
    }

}
