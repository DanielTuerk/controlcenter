package net.wbz.moba.controlcenter.web.client.model.track;

import net.wbz.moba.controlcenter.web.shared.track.model.Switch;

/**
 * @author Daniel Tuerk
 */
public class SwitchRightRightToLeftWidget extends AbstractSwitchRightWidget {
    @Override
    protected Switch.PRESENTATION getPresentation() {
        return Switch.PRESENTATION.RIGHT_TO_LEFT;
    }

    @Override
    public String getTrackWidgetStyleName() {
        return "widget_track_switch_right_right_to_left";
    }

    @Override
    public AbstractSvgTrackWidget<Switch> getClone() {
        return new SwitchRightRightToLeftWidget();
    }

    @Override
    public Switch getNewTrackPart() {
        Switch switchRRightL = new Switch();
        switchRRightL.setCurrentDirection(Switch.DIRECTION.RIGHT);
        switchRRightL.setCurrentPresentation(Switch.PRESENTATION.RIGHT_TO_LEFT);
        return switchRRightL;
    }

}
