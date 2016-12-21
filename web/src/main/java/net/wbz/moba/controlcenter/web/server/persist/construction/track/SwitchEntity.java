package net.wbz.moba.controlcenter.web.server.persist.construction.track;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.googlecode.jmapper.annotations.JMap;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Daniel Tuerk
 */
@Entity
@Table(name = "trackpart_switch")
public class SwitchEntity extends TrackPartEntity {

    public enum DIRECTION implements IsSerializable {RIGHT, LEFT}

    public enum PRESENTATION implements IsSerializable {LEFT_TO_RIGHT, RIGHT_TO_LEFT, BOTTOM_TO_TOP, TOP_TO_BOTTOM}

    public enum STATE implements IsSerializable {STRAIGHT, BRANCH}

    @JMap
    private DIRECTION currentDirection;
    @JMap
    private PRESENTATION currentPresentation;

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
     * {@link SwitchEntity.PRESENTATION} of the switch.
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
