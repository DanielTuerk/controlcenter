package net.wbz.moba.controlcenter.web.client.model.track;

import net.wbz.moba.controlcenter.web.shared.track.model.Turnout;

/**
 * @author Daniel Tuerk
 */
public class TurnoutRightBottomToTopWidget extends AbstractTurnoutRightWidget {
    @Override
    protected Turnout.PRESENTATION getPresentation() {
        return Turnout.PRESENTATION.BOTTOM_TO_TOP;
    }

    @Override
    public String getTrackWidgetStyleName() {
        return "widget_track_switch_right_bottom_to_top";
    }

    @Override
    public AbstractSvgTrackWidget<Turnout> getClone() {
        return new TurnoutRightBottomToTopWidget();
    }

    @Override
    public Turnout getNewTrackPart() {
        Turnout turnoutRBottomT = new Turnout();
        turnoutRBottomT.setCurrentDirection(Turnout.DIRECTION.RIGHT);
        turnoutRBottomT.setCurrentPresentation(Turnout.PRESENTATION.BOTTOM_TO_TOP);
        return turnoutRBottomT;
    }

}
