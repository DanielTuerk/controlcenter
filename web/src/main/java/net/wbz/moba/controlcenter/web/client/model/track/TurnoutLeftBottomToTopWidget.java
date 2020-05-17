package net.wbz.moba.controlcenter.web.client.model.track;

import net.wbz.moba.controlcenter.web.shared.track.model.Turnout;

/**
 * @author Daniel Tuerk
 */
public class TurnoutLeftBottomToTopWidget extends AbstractTurnoutLeftWidget {
    @Override
    protected Turnout.PRESENTATION getPresentation() {
        return Turnout.PRESENTATION.BOTTOM_TO_TOP;
    }

    @Override
    public String getTrackWidgetStyleName() {
        return "widget_track_switch_left_bottom_to_top";
    }

    @Override
    public AbstractSvgTrackWidget<Turnout> getClone() {
        return new TurnoutLeftBottomToTopWidget();
    }

    @Override
    public Turnout getNewTrackPart() {
        Turnout turnoutBottomTop = new Turnout();
        turnoutBottomTop.setCurrentDirection(Turnout.DIRECTION.LEFT);
        turnoutBottomTop.setCurrentPresentation(Turnout.PRESENTATION.BOTTOM_TO_TOP);
        return turnoutBottomTop;
    }

}
