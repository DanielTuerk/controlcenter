package net.wbz.moba.controlcenter.web.client.model.track;

import com.google.common.base.Strings;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import net.wbz.moba.controlcenter.web.client.TrackUtils;
import net.wbz.moba.controlcenter.web.shared.track.model.GridPosition;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackPart;

/**
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
abstract public class AbstractImageTrackWidget<T extends TrackPart> extends Image {

    public AbstractImageTrackWidget() {
        setUrl(getImageUrl());
        addStyleName("widget_track");
        String additionalStyle = getTrackWidgetStyleName();
        if (!Strings.isNullOrEmpty(additionalStyle)) {
            addStyleName(additionalStyle);
        }
    }

    abstract public void initFromTrackPart(T trackPart);

    abstract public boolean isRepresentationOf(T trackPart);

    abstract public String getImageUrl();

    abstract public String getTrackWidgetStyleName();

    abstract public TrackPart getTrackPart(Widget containerWidget, int zoomLevel);

    abstract public String getPaletteTitel();

    abstract public AbstractImageTrackWidget<T> getClone(T trackPart);

    public AbsoluteTrackPosition getTrackPosition(GridPosition gridPosition, int zoomLevel) {
        return new AbsoluteTrackPosition(TrackUtils.getLeftPositionFromX(gridPosition.getX(), zoomLevel),
                TrackUtils.getTopPositionFromY(gridPosition.getY(), zoomLevel));
    }

    public GridPosition getGridPosition(Widget containerWidget, int zoomLevel) {
        return new GridPosition(
                TrackUtils.getXFromLeftPosition(getAbsoluteLeft() - containerWidget.getAbsoluteLeft(), zoomLevel),
                TrackUtils.getYFromTopPosition(getAbsoluteTop() - containerWidget.getAbsoluteTop(), zoomLevel));
    }

}
