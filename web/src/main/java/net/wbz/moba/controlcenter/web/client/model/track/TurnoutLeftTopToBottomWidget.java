package net.wbz.moba.controlcenter.web.client.model.track;

import net.wbz.moba.controlcenter.web.shared.track.model.Turnout;

/**
 * @author Daniel Tuerk
 */
public class TurnoutLeftTopToBottomWidget extends AbstractTurnoutLeftWidget {
    @Override
    protected Turnout.PRESENTATION getPresentation() {
        return Turnout.PRESENTATION.TOP_TO_BOTTOM;
    }

    @Override
    public String getTrackWidgetStyleName() {
        return "widget_track_switch_left_top_to_bottom";
    }

    @Override
    public AbstractSvgTrackWidget<Turnout> getClone() {
        return new TurnoutLeftTopToBottomWidget();
    }

    @Override
    public Turnout getNewTrackPart() {
        Turnout turnoutTopBottom = new Turnout();
        turnoutTopBottom.setCurrentDirection(Turnout.DIRECTION.LEFT);
        turnoutTopBottom.setCurrentPresentation(Turnout.PRESENTATION.TOP_TO_BOTTOM);
        return turnoutTopBottom;
    }

}
