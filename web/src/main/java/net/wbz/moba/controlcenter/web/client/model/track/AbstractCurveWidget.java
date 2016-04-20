package net.wbz.moba.controlcenter.web.client.model.track;

import com.google.gwt.user.client.ui.Widget;
import net.wbz.moba.controlcenter.web.client.RequestUtils;
import net.wbz.moba.controlcenter.web.client.util.SvgTrackUtil;
import net.wbz.moba.controlcenter.web.shared.track.model.Curve;
import net.wbz.moba.controlcenter.web.shared.track.model.CurveProxy;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackPartProxy;
import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGSVGElement;

/**
 * @author Daniel Tuerk
 */
abstract public class AbstractCurveWidget extends AbstractBlockSvgTrackWidget<CurveProxy> {

    @Override
    protected void addSvgContent(OMSVGDocument doc, OMSVGSVGElement svg) {
        svg.appendChild(SvgTrackUtil.createLine(doc, 12f, 26f, 26f, 12f, 5));
    }

    @Override
    public TrackPartProxy getTrackPart(Widget containerWidget, int zoomLevel) {
        CurveProxy curve = RequestUtils.getInstance().getTrackEditorRequest().create(CurveProxy.class);
        curve.setDirection(getCurveDirection());
        curve.setGridPosition(getGridPosition(containerWidget, zoomLevel));
//        curve.setFunctionConfigs(getStoredWidgetFunctionConfigs());
        return curve;
    }

    @Override
    public boolean isRepresentationOf(CurveProxy trackPart) {
        return trackPart.getDirection() == getCurveDirection();
    }

    abstract public Curve.DIRECTION getCurveDirection();

    @Override
    public String getPaletteTitle() {
        return "Curve";
    }
}
