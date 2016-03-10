package net.wbz.moba.controlcenter.web.client.model.track;

import net.wbz.moba.controlcenter.web.shared.track.model.Switch;

/**
 * @author Daniel Tuerk
 */
public class SwitchRightBottomToTopWidget extends AbstractSwitchRightWidget {
    @Override
    protected Switch.PRESENTATION getPresentation() {
        return Switch.PRESENTATION.BOTTOM_TO_TOP;
    }

    @Override
    public String getTrackWidgetStyleName() {
        return "widget_track_switch_right_bottom_to_top";
    }

    @Override
    public AbstractSvgTrackWidget<Switch> getClone() {
        return new SwitchRightBottomToTopWidget();
    }

    @Override
    public Switch getNewTrackPart() {
        Switch switchRBottomT = new Switch();
        switchRBottomT.setCurrentDirection(Switch.DIRECTION.RIGHT);
        switchRBottomT.setCurrentPresentation(Switch.PRESENTATION.BOTTOM_TO_TOP);
        return switchRBottomT;
    }

}
