package net.wbz.moba.controlcenter.web.client.model.track;

import net.wbz.moba.controlcenter.web.shared.track.model.Switch;

/**
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
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
    public AbstractImageTrackWidget<Switch> getClone(Switch trackPart) {
        SwitchRightTopToBottomWidget clone = new SwitchRightTopToBottomWidget();
        clone.initFromTrackPart(trackPart);
        return clone;
    }

}
