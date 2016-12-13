package net.wbz.moba.controlcenter.web.client.model.track;

import net.wbz.moba.controlcenter.web.client.RequestUtils;
import net.wbz.moba.controlcenter.web.shared.editor.TrackEditorRequest;
import net.wbz.moba.controlcenter.web.shared.track.model.Switch;
import net.wbz.moba.controlcenter.web.shared.track.model.SwitchProxy;

/**
 * @author Daniel Tuerk
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
    public AbstractSvgTrackWidget<SwitchProxy> getClone() {
        return new SwitchRightTopToBottomWidget();
    }

    @Override
    public SwitchProxy getNewTrackPart(TrackEditorRequest trackEditorRequest) {
        SwitchProxy switchRTopB = RequestUtils.getInstance().getTrackEditorRequest().create(SwitchProxy.class);
        switchRTopB.setCurrentDirection(Switch.DIRECTION.RIGHT);
        switchRTopB.setCurrentPresentation(Switch.PRESENTATION.TOP_TO_BOTTOM);
        return switchRTopB;
    }

}
