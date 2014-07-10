package net.wbz.moba.controlcenter.web.client.model.track;

import net.wbz.moba.controlcenter.web.shared.track.model.TrackPart;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
public class ModelManager {

    private final static ModelManager instance = new ModelManager();

    private ModelManager() {

    }

    public static ModelManager getInstance() {
        return instance;
    }

    private final List<AbstractSvgTrackWidget> widgets = new ArrayList<AbstractSvgTrackWidget>();

    public void registerModel(AbstractSvgTrackWidget widgetClass) {
        widgets.add(widgetClass);
    }

    public List<AbstractSvgTrackWidget> getWidgets() {
        return widgets;
    }

    public AbstractSvgTrackWidget getWidgetOf(TrackPart trackPart) {
        for (AbstractSvgTrackWidget widget : widgets) {
            try {
                if (widget.isRepresentationOf(trackPart)) {
                    return widget.getClone(trackPart);
                }
            } catch (ClassCastException e) {
                //ignore to search for the next widget which match the {@link TrackPart} type
            }
        }
        return null; //TODO: exception
    }
}
