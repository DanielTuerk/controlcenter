package net.wbz.moba.controlcenter.web.client.model.track;

import net.wbz.moba.controlcenter.web.shared.track.model.Switch;

/**
 * @author Daniel Tuerk
 */
public class SwitchRightTopToBottomWidget extends AbstractSwitchRightWidget {
    @Override
    protected Switch.PRESENTATION getPresentation() {
        return Switch.PRESENTATION.TOP_TO_BOTTOM;
    }

    @Override
    public String getTrackWidgetStyleName() {
        return "widget_track_switch_right_top_to_bottom";
    }

    @Override
    public AbstractSvgTrackWidget<Switch> getClone() {
        return new SwitchRightTopToBottomWidget();
    }

    @Override
    public Switch getNewTrackPart() {
        Switch switchRTopB = new Switch();
        switchRTopB.setCurrentDirection(Switch.DIRECTION.RIGHT);
        switchRTopB.setCurrentPresentation(Switch.PRESENTATION.TOP_TO_BOTTOM);
        return switchRTopB;
    }

}
