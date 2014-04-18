package net.wbz.moba.controlcenter.web.client.model.track;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
public class AbsoluteTrackPosition {

    private int top;
    private int left;

    public AbsoluteTrackPosition( int left,int top) {
        this.top = top;
        this.left = left;
    }

    public int getTop() {
        return top;
    }

    public int getLeft() {
        return left;
    }
}
