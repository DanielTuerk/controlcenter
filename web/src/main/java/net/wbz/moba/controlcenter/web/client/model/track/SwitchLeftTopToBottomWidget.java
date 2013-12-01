package net.wbz.moba.controlcenter.web.client.model.track;

import net.wbz.moba.controlcenter.web.shared.track.model.Switch;

/**
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
public class SwitchLeftTopToBottomWidget extends AbstractSwitchLeftWidget {
    @Override
    protected Switch.PRESENTATION getPresentation() {
        return Switch.PRESENTATION.TOP_TO_BOTTOM;
    }

    @Override
    public String getTrackWidgetStyleName() {
        return "widget_track_switch_left_top_to_bottom";
    }

    @Override
    public AbstractImageTrackWidget<Switch> getClone(Switch trackPart) {
        SwitchLeftTopToBottomWidget clone = new SwitchLeftTopToBottomWidget();
        clone.initFromTrackPart(trackPart);
        return clone;
    }

}
