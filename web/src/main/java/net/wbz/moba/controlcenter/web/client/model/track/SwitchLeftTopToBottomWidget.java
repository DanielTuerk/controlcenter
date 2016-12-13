package net.wbz.moba.controlcenter.web.client.model.track;

import net.wbz.moba.controlcenter.web.client.RequestUtils;
import net.wbz.moba.controlcenter.web.shared.editor.TrackEditorRequest;
import net.wbz.moba.controlcenter.web.shared.track.model.Switch;
import net.wbz.moba.controlcenter.web.shared.track.model.SwitchProxy;

/**
 * @author Daniel Tuerk
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
    public AbstractSvgTrackWidget<SwitchProxy> getClone() {
        return new SwitchLeftTopToBottomWidget();
    }

    @Override
    public SwitchProxy getNewTrackPart(TrackEditorRequest trackEditorRequest) {
        SwitchProxy switchTopBottom = RequestUtils.getInstance().getTrackEditorRequest().create(SwitchProxy.class);
        switchTopBottom.setCurrentDirection(Switch.DIRECTION.LEFT);
        switchTopBottom.setCurrentPresentation(Switch.PRESENTATION.TOP_TO_BOTTOM);
        return switchTopBottom;
    }

}
