package net.wbz.moba.controlcenter.web.client.model.track;

import net.wbz.moba.controlcenter.web.shared.track.model.Switch;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
public class SwitchLeftRightToLeftWidget extends AbstractSwitchLeftWidget {
    @Override
    protected Switch.PRESENTATION getPresentation() {
        return Switch.PRESENTATION.RIGHT_TO_LEFT;
    }

    @Override
    public String getTrackWidgetStyleName() {
        return "widget_track_switch_left_right_to_left";
    }

    @Override
    public AbstractSvgTrackWidget<Switch> getClone(Switch trackPart) {
        SwitchLeftRightToLeftWidget clone = new SwitchLeftRightToLeftWidget();
        clone.initFromTrackPart(trackPart);
        return clone;
    }

}
