package net.wbz.moba.controlcenter.web.client.model.track;

import net.wbz.moba.controlcenter.web.shared.track.model.Turnout;

/**
 * @author Daniel Tuerk
 */
public class TurnoutRightTopToBottomWidget extends AbstractTurnoutRightWidget {
    @Override
    protected Turnout.PRESENTATION getPresentation() {
        return Turnout.PRESENTATION.TOP_TO_BOTTOM;
    }

    @Override
    public String getTrackWidgetStyleName() {
        return "widget_track_switch_right_top_to_bottom";
    }

    @Override
    public AbstractSvgTrackWidget<Turnout> getClone() {
        return new TurnoutRightTopToBottomWidget();
    }

    @Override
    public Turnout getNewTrackPart() {
        Turnout turnoutRTopB = new Turnout();
        turnoutRTopB.setCurrentDirection(Turnout.DIRECTION.RIGHT);
        turnoutRTopB.setCurrentPresentation(Turnout.PRESENTATION.TOP_TO_BOTTOM);
        return turnoutRTopB;
    }

}
