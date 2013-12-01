package net.wbz.moba.controlcenter.web.client.model.track;

import net.wbz.moba.controlcenter.web.shared.track.model.Switch;

/**
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
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
    public AbstractImageTrackWidget<Switch> getClone(Switch trackPart) {
        SwitchRightRightToLeftWidget clone = new SwitchRightRightToLeftWidget();
        clone.initFromTrackPart(trackPart);
        return clone;
    }

}
