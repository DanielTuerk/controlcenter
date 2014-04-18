package net.wbz.moba.controlcenter.web.shared.track.model;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
public class Curve extends TrackPart {

    public enum DIRECTION {BOTTOM_RIGHT, BOTTOM_LEFT, TOP_LEFT, TOP_RIGHT};

    public DIRECTION direction;

    public DIRECTION getDirection() {
        return direction;
    }

    public void setDirection(DIRECTION direction) {
        this.direction = direction;
    }
}
