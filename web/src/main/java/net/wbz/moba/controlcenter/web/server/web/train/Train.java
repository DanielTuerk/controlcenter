package net.wbz.moba.controlcenter.web.server.web.train;

import net.wbz.moba.controlcenter.web.shared.track.model.AbstractDto;

/**
 * @author Daniel Tuerk
 */
public class Train extends AbstractDto{

    private Integer address;
    private String name;

    private int drivingLevel = 0;

    private boolean forward;


    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public int getDrivingLevel() {
        return drivingLevel;
    }

    public void setDrivingLevel(int drivingLevel) {
        this.drivingLevel = drivingLevel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(Integer address) {
        this.address = address;
    }

    public boolean isForward() {
        return forward;
    }

    public void setForward(boolean forward) {
        this.forward = forward;
    }
}
