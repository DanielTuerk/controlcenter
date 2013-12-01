package net.wbz.moba.controlcenter.web.client.model.track;

import net.wbz.moba.controlcenter.web.shared.track.model.Switch;

/**
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
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
    public AbstractImageTrackWidget<Switch> getClone(Switch trackPart) {
        SwitchRightBottomToTopWidget clone = new SwitchRightBottomToTopWidget();
        clone.initFromTrackPart(trackPart);
        return clone;
    }

}
