package net.wbz.moba.controlcenter.web.client.model.track;

import net.wbz.moba.controlcenter.web.shared.track.model.Switch;

/**
 * @author Daniel Tuerk
 */
public class SwitchLeftLeftToRightWidget extends AbstractSwitchLeftWidget {
    @Override
    protected Switch.PRESENTATION getPresentation() {
        return Switch.PRESENTATION.LEFT_TO_RIGHT;
    }

    @Override
    public String getTrackWidgetStyleName() {
        return "widget_track_switch_left_left_to_right";
    }

    @Override
    public AbstractSvgTrackWidget<Switch> getClone() {
        return new SwitchLeftLeftToRightWidget();
    }

    @Override
    public Switch getNewTrackPart() {
        Switch switchLeftR = new Switch();
        switchLeftR.setCurrentDirection(Switch.DIRECTION.LEFT);
        switchLeftR.setCurrentPresentation(Switch.PRESENTATION.LEFT_TO_RIGHT);
        return switchLeftR
                ;
    }
}
