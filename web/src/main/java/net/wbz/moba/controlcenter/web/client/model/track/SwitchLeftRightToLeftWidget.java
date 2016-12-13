package net.wbz.moba.controlcenter.web.client.model.track;

import net.wbz.moba.controlcenter.web.client.RequestUtils;
import net.wbz.moba.controlcenter.web.shared.editor.TrackEditorRequest;
import net.wbz.moba.controlcenter.web.shared.track.model.Switch;
import net.wbz.moba.controlcenter.web.shared.track.model.SwitchProxy;

/**
 * @author Daniel Tuerk
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
    public AbstractSvgTrackWidget<SwitchProxy> getClone() {
        return new SwitchLeftRightToLeftWidget();
    }

    @Override
    public SwitchProxy getNewTrackPart(TrackEditorRequest trackEditorRequest) {
        SwitchProxy switchRightL = RequestUtils.getInstance().getTrackEditorRequest().create(SwitchProxy.class);
        switchRightL.setCurrentDirection(Switch.DIRECTION.LEFT);
        switchRightL.setCurrentPresentation(Switch.PRESENTATION.RIGHT_TO_LEFT);
        return switchRightL;
    }

}
