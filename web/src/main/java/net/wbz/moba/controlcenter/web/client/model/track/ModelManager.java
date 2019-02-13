package net.wbz.moba.controlcenter.web.client.model.track;

import java.util.ArrayList;
import java.util.List;
import net.wbz.moba.controlcenter.web.client.model.track.block.BlockStraightHorizontalWidget;
import net.wbz.moba.controlcenter.web.client.model.track.block.BlockStraightVerticalWidget;
import net.wbz.moba.controlcenter.web.client.model.track.signal.SignalHorizontalWidget;
import net.wbz.moba.controlcenter.web.client.model.track.signal.SignalVerticalWidget;
import net.wbz.moba.controlcenter.web.shared.track.model.AbstractTrackPart;

/**
 * @author Daniel Tuerk
 */
public class ModelManager {

    private final static ModelManager instance = new ModelManager();
    private final List<AbstractSvgTrackWidget> widgets = new ArrayList<>();

    private ModelManager() {
    }

    public static ModelManager getInstance() {
        return instance;
    }

    public void registerModel(AbstractSvgTrackWidget widgetClass) {
        widgetClass.initFromTrackPart(widgetClass.getNewTrackPart());
        widgets.add(widgetClass);
    }

    public List<AbstractSvgTrackWidget> getWidgets() {
        return widgets;
    }

    public AbstractSvgTrackWidget getWidgetOf(AbstractTrackPart trackPart) {
        for (AbstractSvgTrackWidget widget : widgets) {
            try {
                if (widget.isRepresentationOf(trackPart)) {
                    return widget.getClone(trackPart);
                }
            } catch (ClassCastException e) {
                // ignore to search for the next widget which match the {@link AbstractTrackPart} type
            }
        }
        throw new RuntimeException(
            "no widget found for AbstractTrackPart class '" + trackPart.getClass().getName() + "'");
    }

    public void init() {
        // TODO: ugly ugly ugly bullshit

        registerModel(new SignalHorizontalWidget());
        registerModel(new SignalVerticalWidget());

        registerModel(new UncouplerHorizontalWidget());
        registerModel(new UncouplerVerticalWidget());

        registerModel(new BlockStraightHorizontalWidget());
        registerModel(new BlockStraightVerticalWidget());

        // straight after signal because signal extends straight
        registerModel(new StraightVerticalWidget());
        registerModel(new StraightHorizontalWidget());

        registerModel(new CurveTopRightWidget());
        registerModel(new CurveTopLeftWidget());
        registerModel(new CurveBottomRightWidget());
        registerModel(new CurveBottomLeftWidget());

        registerModel(new SwitchLeftBottomToTopWidget());
        registerModel(new SwitchLeftTopToBottomWidget());
        registerModel(new SwitchLeftRightToLeftWidget());
        registerModel(new SwitchLeftLeftToRightWidget());
        registerModel(new SwitchRightBottomToTopWidget());
        registerModel(new SwitchRightTopToBottomWidget());
        registerModel(new SwitchRightRightToLeftWidget());
        registerModel(new SwitchRightLeftToRightWidget());
    }
}
