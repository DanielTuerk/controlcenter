package net.wbz.moba.controlcenter.web.client;

import net.wbz.moba.controlcenter.web.client.editor.track.TrackEditorContainer;

/**
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
public class TrackUtils {

    public static int getTopPositionFromY(int y, int zoomLevel) {
        return (TrackEditorContainer.draggableOffsetHeight + (zoomLevel * TrackEditorContainer.ZOOM_STEP)) * y;
    }


    public static int getLeftPositionFromX(int x, int zoomLevel) {
        return (TrackEditorContainer.draggableOffsetWidth + (zoomLevel * TrackEditorContainer.ZOOM_STEP)) * x;
    }

    public static int getYFromTopPosition(int topPos, int zoomLevel) {
        return topPos / TrackEditorContainer.draggableOffsetHeight + (zoomLevel * TrackEditorContainer.ZOOM_STEP);
    }


    public static int getXFromLeftPosition(int leftPos, int zoomLevel) {
        return leftPos / (TrackEditorContainer.draggableOffsetWidth + (zoomLevel * TrackEditorContainer.ZOOM_STEP));
    }
}
