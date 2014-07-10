package net.wbz.moba.controlcenter.web.client.model.track;

import net.wbz.moba.controlcenter.web.client.util.SvgTrackUtil;
import net.wbz.moba.controlcenter.web.shared.track.model.Switch;
import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGLineElement;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.utils.SVGConstants;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
abstract public class AbstractSwitchLeftWidget extends AbstractSwitchWidget {

    @Override
    protected void addSvgContent(OMSVGDocument doc, OMSVGSVGElement svg) {
        svg.appendChild(SvgTrackUtil.createRectangle(doc,0f, 10f, 25f, 5f));
        svg.appendChild(SvgTrackUtil.createLine(doc, 0f, 12.5f, 12.5f, 0f, 2));
        svg.appendChild(SvgTrackUtil.createLine(doc, 6f, 12.5f, 18.5f,0f, 2));
    }

    @Override
    protected void addActiveStateSvgContent(OMSVGDocument doc, OMSVGSVGElement svg) {
        svg.appendChild(SvgTrackUtil.createRectangle(doc, 0f, 10f, 25f, 5f));
        svg.appendChild(SvgTrackUtil.createLine(doc, 12.5f, 25f, 12.5f, 5f, 2));
        svg.appendChild(SvgTrackUtil.createLine(doc, 0f, 17.5f, 25f, 17.5f, 2));
    }

    @Override
    protected Switch.DIRECTION getDirection() {
        return Switch.DIRECTION.LEFT;
    }
}
