package net.wbz.moba.controlcenter.web.shared.track.model;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
public class Switch extends TrackPart {

    private DIRECTION currentDirection;

    private PRESENTATION currentPresentation;


    public enum STATE implements IsSerializable {STRAIGHT, BRANCH}

    ;

    public enum DIRECTION implements IsSerializable {RIGHT, LEFT}

    ;

    public enum PRESENTATION implements IsSerializable {LEFT_TO_RIGHT, RIGHT_TO_LEFT, BOTTOM_TO_TOP, TOP_TO_BOTTOM}

    ;

    public void turn(DIRECTION line) {

    }

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

    public long getId() {
        return 0L;
    }


}
