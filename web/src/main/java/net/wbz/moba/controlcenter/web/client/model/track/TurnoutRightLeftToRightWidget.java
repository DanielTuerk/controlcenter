package net.wbz.moba.controlcenter.web.client.model.track;

import net.wbz.moba.controlcenter.web.shared.track.model.Turnout;

/**
 * @author Daniel Tuerk
 */
public class TurnoutRightLeftToRightWidget extends AbstractTurnoutRightWidget {
    @Override
    protected Turnout.PRESENTATION getPresentation() {
        return Turnout.PRESENTATION.LEFT_TO_RIGHT;
    }

    @Override
    public String getTrackWidgetStyleName() {
        return "widget_track_switch_right_left_to_right";
    }

    @Override
    public AbstractSvgTrackWidget<Turnout> getClone() {
        return new TurnoutRightLeftToRightWidget();
    }

    @Override
    public Turnout getNewTrackPart() {
        Turnout turnoutRLeftR = new Turnout();
        turnoutRLeftR.setCurrentDirection(Turnout.DIRECTION.RIGHT);
        turnoutRLeftR.setCurrentPresentation(Turnout.PRESENTATION.LEFT_TO_RIGHT);
        return turnoutRLeftR;
    }

}
