package net.wbz.moba.controlcenter.web.client.model.track;

import net.wbz.moba.controlcenter.web.shared.track.model.Turnout;

/**
 * @author Daniel Tuerk
 */
public class TurnoutLeftLeftToRightWidget extends AbstractTurnoutLeftWidget {
    @Override
    protected Turnout.PRESENTATION getPresentation() {
        return Turnout.PRESENTATION.LEFT_TO_RIGHT;
    }

    @Override
    public String getTrackWidgetStyleName() {
        return "widget_track_switch_left_left_to_right";
    }

    @Override
    public AbstractSvgTrackWidget<Turnout> getClone() {
        return new TurnoutLeftLeftToRightWidget();
    }

    @Override
    public Turnout getNewTrackPart() {
        Turnout turnoutLeftR = new Turnout();
        turnoutLeftR.setCurrentDirection(Turnout.DIRECTION.LEFT);
        turnoutLeftR.setCurrentPresentation(Turnout.PRESENTATION.LEFT_TO_RIGHT);
        return turnoutLeftR;
    }
}
