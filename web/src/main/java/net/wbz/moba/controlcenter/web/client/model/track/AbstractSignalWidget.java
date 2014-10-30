package net.wbz.moba.controlcenter.web.client.model.track;

import com.google.gwt.user.client.ui.Widget;
import net.wbz.moba.controlcenter.web.client.util.SvgTrackUtil;
import net.wbz.moba.controlcenter.web.shared.track.model.Signal;
import net.wbz.moba.controlcenter.web.shared.track.model.Straight;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackPart;
import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.utils.SVGConstants;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
abstract public class AbstractSignalWidget extends AbstractControlSvgTrackWidget<Signal> {

    @Override
    protected void addSvgContent(OMSVGDocument doc, OMSVGSVGElement svg) {
        addSvgContent(doc, svg, SVGConstants.CSS_WHITE_VALUE, SVGConstants.CSS_GREEN_VALUE);
    }

    @Override
    protected void addActiveStateSvgContent(OMSVGDocument doc, OMSVGSVGElement svg) {
        addSvgContent(doc, svg, SVGConstants.CSS_RED_VALUE, SVGConstants.CSS_WHITE_VALUE);
    }

    private void addSvgContent(OMSVGDocument doc, OMSVGSVGElement svg, String color, String colorActive) {
        svg.appendChild(SvgTrackUtil.createRectangle(doc, 0f, 10f, 25f, 5f));
        svg.appendChild(SvgTrackUtil.createCircle(doc, 12.5f, 6f, 5f, color));
        svg.appendChild(SvgTrackUtil.createCircle(doc, 12.5f, 19f, 5f, colorActive));
    }

    @Override
    public TrackPart getTrackPart(Widget containerWidget, int zoomLevel) {
        Signal signal = new Signal();
        signal.setDirection(getStraightDirection());
        signal.setGridPosition(getGridPosition(containerWidget, zoomLevel));
        signal.setConfiguration(getStoredWidgetConfiguration());
        return signal;
    }

    abstract public Straight.DIRECTION getStraightDirection();

    @Override
    public String getPaletteTitle() {
        return "Signal";
    }

}
