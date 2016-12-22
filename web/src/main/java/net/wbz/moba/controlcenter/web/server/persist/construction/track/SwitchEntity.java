package net.wbz.moba.controlcenter.web.server.persist.construction.track;

import com.googlecode.jmapper.annotations.JMap;
import net.wbz.moba.controlcenter.web.shared.track.model.Switch;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Daniel Tuerk
 */
@Entity
@Table(name = "trackpart_switch")
public class SwitchEntity extends TrackPartEntity {

//    public enum DIRECTION implements IsSerializable {RIGHT, LEFT}
//
//    public enum PRESENTATION implements IsSerializable {LEFT_TO_RIGHT, RIGHT_TO_LEFT, BOTTOM_TO_TOP, TOP_TO_BOTTOM}
//
//    public enum STATE implements IsSerializable {STRAIGHT, BRANCH}

    @JMap
    private Switch.DIRECTION currentDirection;
    @JMap
    private Switch.PRESENTATION currentPresentation;

    public void setCurrentDirection(Switch.DIRECTION currentDirection) {
        this.currentDirection = currentDirection;
    }

    public void setCurrentPresentation(Switch.PRESENTATION currentPresentation) {
        this.currentPresentation = currentPresentation;
    }

    public Switch.PRESENTATION getCurrentPresentation() {
        return currentPresentation;
    }

    public Switch.DIRECTION getCurrentDirection() {
        return currentDirection;
    }


}
