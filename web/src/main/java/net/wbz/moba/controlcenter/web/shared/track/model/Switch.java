package net.wbz.moba.controlcenter.web.shared.track.model;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author Daniel Tuerk
 */
public class Switch extends TrackPart {

    public enum DIRECTION implements IsSerializable {RIGHT, LEFT}

    public enum PRESENTATION implements IsSerializable {LEFT_TO_RIGHT, RIGHT_TO_LEFT, BOTTOM_TO_TOP, TOP_TO_BOTTOM}

    public enum STATE implements IsSerializable {STRAIGHT, BRANCH}


    private Switch.DIRECTION currentDirection;
    private Switch.PRESENTATION currentPresentation;

    public DIRECTION getCurrentDirection() {
        return currentDirection;
    }

    public void setCurrentDirection(DIRECTION currentDirection) {
        this.currentDirection = currentDirection;
    }

    public PRESENTATION getCurrentPresentation() {
        return currentPresentation;
    }

    public void setCurrentPresentation(PRESENTATION currentPresentation) {
        this.currentPresentation = currentPresentation;
    }

    /**
     * Rotation angle in degree of the current
     * {@link Switch.PRESENTATION} of the switch.
     *
     * @return angle in degree
     */
    @Override
    public double getRotationAngle() {
        switch (getCurrentPresentation()) {
            case LEFT_TO_RIGHT:
                return 0d;
            case RIGHT_TO_LEFT:
                return 180d;
            case BOTTOM_TO_TOP:
                return 270d;
            case TOP_TO_BOTTOM:
                return 90d;
            default:
                return 0d;
        }
    }
}
