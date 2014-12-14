package net.wbz.moba.controlcenter.web.client.model.track;

import net.wbz.moba.controlcenter.web.client.ServiceUtils;
import net.wbz.moba.controlcenter.web.client.editor.track.ClickActionViewerWidgetHandler;
import net.wbz.moba.controlcenter.web.client.util.EmptyCallback;
import net.wbz.moba.controlcenter.web.shared.track.model.Configuration;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackPart;
import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGSVGElement;

/**
 * A {@link AbstractSvgTrackWidget} with click control
 * to toggle the state of the {@link net.wbz.moba.controlcenter.web.shared.track.model.TrackPart}.
 * <p/>
 * TODO: disable widget control if no connection is established
 *
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
abstract public class AbstractControlSvgTrackWidget<T extends TrackPart> extends AbstractSvgTrackWidget<T>
        implements ClickActionViewerWidgetHandler {

    /**
     * Current state of the widget to toggle.
     */
    private boolean trackPartState = false;

    /**
     * Add the svg items to the element which represents the active state of the widget.
     *
     * @param doc {@link org.vectomatic.dom.svg.OMSVGDocument}
     * @param svg {@link org.vectomatic.dom.svg.OMSVGSVGElement}
     */
    abstract protected void addActiveStateSvgContent(OMSVGDocument doc, OMSVGSVGElement svg);

    @Override
    public void onClick() {
        if (isEnabled()) {
            Configuration toggleFunctionConfig = getStoredWidgetFunctionConfigs().get(TrackPart.DEFAULT_TOGGLE_FUNCTION);
            ServiceUtils.getTrackViewerService().toggleTrackPart(toggleFunctionConfig, !trackPartState, new
                    EmptyCallback<Void>());
        }
    }

    @Override
    public void updateFunctionState(Configuration configuration, boolean state) {
        // update the SVG for the state of the {@link TrackPart#DEFAULT_TOGGLE_FUNCTION}
        Configuration toggleFunctionConfig = getStoredWidgetFunctionConfigs().get(TrackPart.DEFAULT_TOGGLE_FUNCTION);
        if (toggleFunctionConfig.equals(configuration)) {
            trackPartState = state;
            clearSvgContent();
            if (state == toggleFunctionConfig.isBitState()) {
                addActiveStateSvgContent(getSvgDocument(), getSvgRootElement());
            } else {
                addSvgContent(getSvgDocument(), getSvgRootElement());
            }
        }
    }
}
