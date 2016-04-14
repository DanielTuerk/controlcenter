package net.wbz.moba.controlcenter.web.client.model.track;

import net.wbz.moba.controlcenter.web.client.ServiceUtils;
import net.wbz.moba.controlcenter.web.shared.track.model.Switch;
import net.wbz.moba.controlcenter.web.shared.track.model.SwitchProxy;

/**
 * @author Daniel Tuerk
 */
public class SwitchLeftBottomToTopWidget extends AbstractSwitchLeftWidget {
    @Override
    protected Switch.PRESENTATION getPresentation() {
        return Switch.PRESENTATION.BOTTOM_TO_TOP;
    }

    @Override
    public String getTrackWidgetStyleName() {
        return "widget_track_switch_left_bottom_to_top";
    }

    @Override
    public AbstractSvgTrackWidget<SwitchProxy> getClone() {
        return new SwitchLeftBottomToTopWidget();
    }

    @Override
    public SwitchProxy getNewTrackPart() {
        SwitchProxy switchBottomTop = ServiceUtils.getInstance().getTrackEditorService().create(SwitchProxy.class);
        switchBottomTop.setCurrentDirection(Switch.DIRECTION.LEFT);
        switchBottomTop.setCurrentPresentation(Switch.PRESENTATION.BOTTOM_TO_TOP);
        return switchBottomTop;
    }

}
