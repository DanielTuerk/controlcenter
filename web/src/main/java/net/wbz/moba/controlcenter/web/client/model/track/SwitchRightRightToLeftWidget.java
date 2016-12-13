package net.wbz.moba.controlcenter.web.client.model.track;

import net.wbz.moba.controlcenter.web.client.RequestUtils;
import net.wbz.moba.controlcenter.web.shared.editor.TrackEditorRequest;
import net.wbz.moba.controlcenter.web.shared.track.model.Switch;
import net.wbz.moba.controlcenter.web.shared.track.model.SwitchProxy;

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
    public AbstractSvgTrackWidget<SwitchProxy> getClone() {
        return new SwitchRightRightToLeftWidget();
    }

    @Override
    public SwitchProxy getNewTrackPart(TrackEditorRequest trackEditorRequest) {
        SwitchProxy switchRRightL = RequestUtils.getInstance().getTrackEditorRequest().create(SwitchProxy.class);
        switchRRightL.setCurrentDirection(Switch.DIRECTION.RIGHT);
        switchRRightL.setCurrentPresentation(Switch.PRESENTATION.RIGHT_TO_LEFT);
        return switchRRightL;
    }

}
