package net.wbz.moba.controlcenter.web.client.model.track;

import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGSVGElement;

import net.wbz.moba.controlcenter.web.client.util.SvgTrackUtil;
import net.wbz.moba.controlcenter.web.shared.track.model.Switch;

/**
 * @author Daniel Tuerk
 */
abstract public class AbstractSwitchLeftWidget extends AbstractSwitchWidget {

    @Override
    protected void addSvgContent(OMSVGDocument doc, OMSVGSVGElement svg, String color) {
        svg.appendChild(SvgTrackUtil.createRectangle(doc, 0f, 10f, 25f, 5f, color));
        svg.appendChild(SvgTrackUtil.createLine(doc, 0f, 12.5f, 10f, 0f, 2, color));
        svg.appendChild(SvgTrackUtil.createLine(doc, 5f, 12.5f, 15f, 0f, 2, color));
    }

    @Override
    protected void addActiveStateSvgContent(OMSVGDocument doc, OMSVGSVGElement svg, String color) {
        svg.appendChild(SvgTrackUtil.createLine(doc, 0f, 13f, 14f, 0f, 5, color));
        svg.appendChild(SvgTrackUtil.createLine(doc, 0f, 10f, 25f, 10f, 2, color));
        svg.appendChild(SvgTrackUtil.createLine(doc, 0f, 15f, 25f, 15f, 2, color));
    }

    @Override
    protected Switch.DIRECTION getDirection() {
        return Switch.DIRECTION.LEFT;
    }
}
