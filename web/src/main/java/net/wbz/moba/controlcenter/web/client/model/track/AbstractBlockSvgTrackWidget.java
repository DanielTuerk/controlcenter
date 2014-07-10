package net.wbz.moba.controlcenter.web.client.model.track;

import net.wbz.moba.controlcenter.web.client.editor.track.EditTrackWidgetHandler;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackPart;

/**
 *
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
abstract public class AbstractBlockSvgTrackWidget<T extends TrackPart> extends AbstractSvgTrackWidget<T>
        implements EditTrackWidgetHandler, BlockPart {

    @Override
    public void freeBlock() {
        removeStyleName("unknownBlock");
        removeStyleName("usedBlock");
        addStyleName("freeBlock");
    }

    @Override
    public void unknownBlock() {
        addStyleName("unknownBlock");
        removeStyleName("usedBlock");
        removeStyleName("freeBlock");
    }

    @Override
    public void usedBlock() {
        removeStyleName("unknownBlock");
        addStyleName("usedBlock");
        removeStyleName("freeBlock");
    }


}
