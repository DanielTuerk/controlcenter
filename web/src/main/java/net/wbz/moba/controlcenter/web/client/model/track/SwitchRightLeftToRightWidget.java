package net.wbz.moba.controlcenter.web.client.model.track;

import net.wbz.moba.controlcenter.web.shared.track.model.Switch;

/**
 * @author Daniel Tuerk
 */
public class SwitchRightLeftToRightWidget extends AbstractSwitchRightWidget {
    @Override
    protected Switch.PRESENTATION getPresentation() {
        return Switch.PRESENTATION.LEFT_TO_RIGHT;
    }

    @Override
    public String getTrackWidgetStyleName() {
        return "widget_track_switch_right_left_to_right";
    }

    @Override
    public AbstractSvgTrackWidget<Switch> getClone() {
        return new SwitchRightLeftToRightWidget();
    }

    @Override
    public Switch getNewTrackPart() {
        Switch switchRLeftR = new Switch();
        switchRLeftR.setCurrentDirection(Switch.DIRECTION.RIGHT);
        switchRLeftR.setCurrentPresentation(Switch.PRESENTATION.LEFT_TO_RIGHT);
        return switchRLeftR;
    }

}
