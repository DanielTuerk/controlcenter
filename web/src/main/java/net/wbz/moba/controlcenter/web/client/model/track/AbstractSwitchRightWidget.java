package net.wbz.moba.controlcenter.web.client.model.track;

import net.wbz.moba.controlcenter.web.client.util.SvgTrackUtil;
import net.wbz.moba.controlcenter.web.shared.track.model.Switch;
import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGSVGElement;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
abstract public class AbstractSwitchRightWidget extends AbstractSwitchWidget {

    @Override
    protected Switch.DIRECTION getDirection() {
        return Switch.DIRECTION.RIGHT;
    }

    @Override
    protected void addSvgContent(OMSVGDocument doc, OMSVGSVGElement svg) {
        svg.appendChild(SvgTrackUtil.createRectangle(doc, 0f, 10f, 25f, 5f));
        svg.appendChild(SvgTrackUtil.createLine(doc, 0f, 10f, 10f, 25f, 2));
        svg.appendChild(SvgTrackUtil.createLine(doc, 5f, 10f, 15f, 25f, 2));
    }

    @Override
    protected void addActiveStateSvgContent(OMSVGDocument doc, OMSVGSVGElement svg) {
        svg.appendChild(SvgTrackUtil.createLine(doc, 0f, 10f, 10f, 26f, 5));
        svg.appendChild(SvgTrackUtil.createLine(doc, 0f, 10f, 25f, 10f, 2));
        svg.appendChild(SvgTrackUtil.createLine(doc, 0f, 10f, 25f, 15f, 2));
    }

}
