package net.wbz.moba.controlcenter.web.shared.track.model;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
public class Straight extends TrackPart {

    public enum DIRECTION {HORIZONTAL, VERTICAL};

    public DIRECTION direction;

    public DIRECTION getDirection() {
        return direction;
    }

    public void setDirection(DIRECTION direction) {
        this.direction = direction;
    }
}
