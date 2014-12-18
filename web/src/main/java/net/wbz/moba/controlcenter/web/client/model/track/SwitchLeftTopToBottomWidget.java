package net.wbz.moba.controlcenter.web.client.model.track;

import net.wbz.moba.controlcenter.web.shared.track.model.Switch;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
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
    public AbstractSvgTrackWidget<Switch> getClone() {
        return new SwitchLeftTopToBottomWidget();
    }

    @Override
    public Switch getNewTrackPart() {
        Switch switchTopBottom = new Switch();
        switchTopBottom.setCurrentDirection(Switch.DIRECTION.LEFT);
        switchTopBottom.setCurrentPresentation(Switch.PRESENTATION.TOP_TO_BOTTOM);
        return switchTopBottom;
    }

}
