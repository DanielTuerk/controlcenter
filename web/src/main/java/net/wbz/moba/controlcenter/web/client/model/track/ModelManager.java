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

    private final List<AbstractImageTrackWidget> widgets = new ArrayList<AbstractImageTrackWidget>();

    public void registerModel(AbstractImageTrackWidget widgetClass) {
        widgets.add(widgetClass);
    }

    public List<AbstractImageTrackWidget> getWidgets() {
        return widgets;
    }

    public AbstractImageTrackWidget getWidgetOf(TrackPart trackPart) {
        for (AbstractImageTrackWidget widget : widgets) {
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
