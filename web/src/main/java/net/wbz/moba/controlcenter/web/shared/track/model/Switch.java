package net.wbz.moba.controlcenter.web.shared.track.model;

import com.google.gwt.user.client.rpc.IsSerializable;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
@Entity
@Table(name = "trackpart_switch")
public class Switch extends TrackPart {

    public enum DIRECTION implements IsSerializable {RIGHT, LEFT}

    public enum PRESENTATION implements IsSerializable {LEFT_TO_RIGHT, RIGHT_TO_LEFT, BOTTOM_TO_TOP, TOP_TO_BOTTOM}

    private DIRECTION currentDirection;

    private PRESENTATION currentPresentation;

    public enum STATE implements IsSerializable {STRAIGHT, BRANCH}


    public void setCurrentDirection(DIRECTION currentDirection) {
        this.currentDirection = currentDirection;
    }

    public void setCurrentPresentation(PRESENTATION currentPresentation) {
        this.currentPresentation = currentPresentation;
    }

    public PRESENTATION getCurrentPresentation() {
        return currentPresentation;
    }

    public DIRECTION getCurrentDirection() {
        return currentDirection;
    }

    /**
     * Rotation angle in degree of the current
     * {@link net.wbz.moba.controlcenter.web.shared.track.model.Switch.PRESENTATION} of the switch.
     *
     * @return angle in degree
     */
    @Override
    public double getRotationAngle() {
        switch (getCurrentPresentation()) {
            case LEFT_TO_RIGHT:
                return 0d;
            case RIGHT_TO_LEFT:
                return  180d;
            case BOTTOM_TO_TOP:
                return  270d;
            case TOP_TO_BOTTOM:
                return  90d;
            default:
                return 0d;
        }
    }
}
