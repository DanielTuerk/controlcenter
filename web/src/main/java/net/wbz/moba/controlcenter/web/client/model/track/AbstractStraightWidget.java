package net.wbz.moba.controlcenter.web.client.model.track;

import net.wbz.moba.controlcenter.web.client.util.SvgTrackUtil;
import net.wbz.moba.controlcenter.web.shared.editor.TrackEditorRequest;
import net.wbz.moba.controlcenter.web.shared.track.model.Straight;
import net.wbz.moba.controlcenter.web.shared.track.model.StraightProxy;
import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGSVGElement;

/**
 * @author Daniel Tuerk
 */
abstract public class AbstractStraightWidget extends AbstractBlockSvgTrackWidget<StraightProxy> {

    @Override
    protected void addSvgContent(OMSVGDocument doc, OMSVGSVGElement svg) {
        svg.appendChild(SvgTrackUtil.createRectangle(getSvgDocument(), 0f, 10f, 25f, 5f));
    }

    @Override
    public StraightProxy getNewTrackPart(TrackEditorRequest trackEditorRequest) {
        StraightProxy straight = trackEditorRequest.create(StraightProxy.class);
//        EmployeeProxy newEmployee = request.create(EmployeeProxy.class);
//        Straight straight = new Straight();
        straight.setDirection(getStraightDirection());
        //TODO
//        return straight;
        return straight;
    }

    abstract public Straight.DIRECTION getStraightDirection();

    @Override
    public String getPaletteTitle() {
        return "Straight";
    }


}
