package net.wbz.moba.controlcenter.web.client.model.track;

import net.wbz.moba.controlcenter.web.client.RequestUtils;
import net.wbz.moba.controlcenter.web.client.util.SvgTrackUtil;
import net.wbz.moba.controlcenter.web.shared.editor.TrackEditorRequest;
import net.wbz.moba.controlcenter.web.shared.track.model.Straight;
import net.wbz.moba.controlcenter.web.shared.track.model.UncouplerProxy;
import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.utils.SVGConstants;

/**
 * @author Daniel Tuerk
 */
abstract public class AbstractUncouplerWidget extends AbstractControlSvgTrackWidget<UncouplerProxy> {

    @Override
    protected void addSvgContent(OMSVGDocument doc, OMSVGSVGElement svg) {
        svg.appendChild(SvgTrackUtil.createLine(doc, 0f, 10f, 12.5f, 14f, 5, SvgTrackUtil.TRACK_COLOR));
        svg.appendChild(SvgTrackUtil.createLine(doc, 12.5f, 14f, 25f, 10f, 5, SvgTrackUtil.TRACK_COLOR));
    }

    @Override
    protected void addActiveStateSvgContent(OMSVGDocument doc, OMSVGSVGElement svg) {
        svg.appendChild(SvgTrackUtil.createLine(doc, 0f, 10f, 12.5f, 6f, 5, SVGConstants.CSS_RED_VALUE));
        svg.appendChild(SvgTrackUtil.createLine(doc, 12.5f, 6f, 25f, 10f, 5, SVGConstants.CSS_RED_VALUE));
    }

    @Override
    public UncouplerProxy getNewTrackPart(TrackEditorRequest trackEditorRequest) {
        UncouplerProxy uncoupler = RequestUtils.getInstance().getTrackEditorRequest().create(UncouplerProxy.class);
        uncoupler.setDirection(getStraightDirection());
        return uncoupler;
    }

    abstract public Straight.DIRECTION getStraightDirection();

    @Override
    public String getPaletteTitle() {
        return "Uncoupler";
    }


}
