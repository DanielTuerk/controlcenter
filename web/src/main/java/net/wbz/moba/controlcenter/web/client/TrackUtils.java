package net.wbz.moba.controlcenter.web.client;

import net.wbz.moba.controlcenter.web.client.editor.track.TrackEditorContainer;

/**
 * @author Daniel Tuerk
 */
public class TrackUtils {

    public static int getTopPositionFromY(int y, int zoomLevel) {
        return (TrackEditorContainer.DRAGGABLE_OFFSET_HEIGHT + (zoomLevel * TrackEditorContainer.ZOOM_STEP)) * y;
    }

    public static int getLeftPositionFromX(int x, int zoomLevel) {
        return (TrackEditorContainer.DRAGGABLE_OFFSET_WIDTH + (zoomLevel * TrackEditorContainer.ZOOM_STEP)) * x;
    }

    public static int getYFromTopPosition(int topPos, int zoomLevel) {
        return topPos / TrackEditorContainer.DRAGGABLE_OFFSET_HEIGHT + (zoomLevel * TrackEditorContainer.ZOOM_STEP);
    }

    public static int getXFromLeftPosition(int leftPos, int zoomLevel) {
        return leftPos / (TrackEditorContainer.DRAGGABLE_OFFSET_WIDTH + (zoomLevel * TrackEditorContainer.ZOOM_STEP));
    }
}
