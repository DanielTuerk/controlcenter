package net.wbz.moba.controlcenter.web.client.model.track;

import com.google.gwt.user.client.ui.Widget;
import net.wbz.moba.controlcenter.web.client.util.SvgTrackUtil;
import net.wbz.moba.controlcenter.web.shared.track.model.Straight;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackPart;
import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGSVGElement;

/**
 * @author Daniel Tuerk
 */
abstract public class AbstractStraightWidget extends AbstractBlockSvgTrackWidget<Straight> {

    @Override
    protected void addSvgContent(OMSVGDocument doc, OMSVGSVGElement svg) {
        svg.appendChild(SvgTrackUtil.createRectangle(getSvgDocument(), 0f, 10f, 25f, 5f));
    }

    @Override
    public Straight getNewTrackPart() {
        Straight straight = new Straight();
        straight.setDirection(getStraightDirection());
        return straight;
    }

    abstract public Straight.DIRECTION getStraightDirection();

    @Override
    public String getPaletteTitle() {
        return "Straight";
    }


}
