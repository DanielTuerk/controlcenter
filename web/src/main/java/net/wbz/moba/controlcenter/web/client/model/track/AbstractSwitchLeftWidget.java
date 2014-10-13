package net.wbz.moba.controlcenter.web.client.model.track;

import net.wbz.moba.controlcenter.web.client.util.SvgTrackUtil;
import net.wbz.moba.controlcenter.web.shared.track.model.Switch;
import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGSVGElement;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
abstract public class AbstractSwitchLeftWidget extends AbstractSwitchWidget {

    @Override
    protected void addSvgContent(OMSVGDocument doc, OMSVGSVGElement svg) {
        svg.appendChild(SvgTrackUtil.createRectangle(doc, 0f, 10f, 25f, 5f));
        svg.appendChild(SvgTrackUtil.createLine(doc, 0f, 12.5f, 12.5f, 0f, 2));
        svg.appendChild(SvgTrackUtil.createLine(doc, 6f, 12.5f, 18.5f, 0f, 2));
    }

    @Override
    protected void addActiveStateSvgContent(OMSVGDocument doc, OMSVGSVGElement svg) {
        svg.appendChild(SvgTrackUtil.createLine(doc, 0f, 10f, 15f, 0f, 5));
        svg.appendChild(SvgTrackUtil.createLine(doc, 0f, 10f, 25f, 10f, 2));
        svg.appendChild(SvgTrackUtil.createLine(doc, 0f, 14f, 25f, 14f, 2));
    }

    @Override
    protected Switch.DIRECTION getDirection() {
        return Switch.DIRECTION.LEFT;
    }
}
