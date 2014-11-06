package net.wbz.moba.controlcenter.web.client.model.track;

import com.google.gwt.user.client.rpc.AsyncCallback;
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
 *
 * TODO: disable widget control if no connection is established
 *
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
abstract public class AbstractControlSvgTrackWidget<T extends TrackPart> extends AbstractSvgTrackWidget<T>
        implements ClickActionViewerWidgetHandler {

    /**
     * Current state of the widget to toggle and repaint.
     */
    private boolean trackPartState = false;

    @Override
    public void repaint(boolean state) {
        if (trackPartState != state) {
            trackPartState = state;

            clearSvgContent();
            if (!trackPartState) {
                addSvgContent(getSvgDocument(), getSvgRootElement());
            } else {
                addActiveStateSvgContent(getSvgDocument(), getSvgRootElement());
            }
        }
    }

    /**
     * Add the svg items to the element which represents the active state of the widget.
     *
     * @param doc {@link org.vectomatic.dom.svg.OMSVGDocument}
     * @param svg {@link org.vectomatic.dom.svg.OMSVGSVGElement}
     */
    abstract protected void addActiveStateSvgContent(OMSVGDocument doc, OMSVGSVGElement svg);

    @Override
    public void onClick() {
        ServiceUtils.getTrackViewerService().toggleTrackPart(getStoredWidgetConfiguration(), !trackPartState, new EmptyCallback<Void>());
    }

}
