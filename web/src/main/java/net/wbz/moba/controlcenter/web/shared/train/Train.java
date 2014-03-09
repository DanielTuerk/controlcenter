package net.wbz.moba.controlcenter.web.shared.train;

import net.wbz.moba.controlcenter.web.shared.AbstractIdModel;

import java.util.List;
import java.util.Map;

/**
 * Created by Daniel on 08.03.14.
 */
public class Train extends AbstractIdModel {

    private String name;

    private int address = -1;

    public enum DIRECTION {BACKWARD, FORWARD}

    private DIRECTION drivingDirection;

    private List<TrainFunction> functions;

    private int drivingLevel;

    public Train() {
    }

    public Train(String name) {
        this.name = name;
    }

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

    public DIRECTION getDrivingDirection() {
        return drivingDirection;
    }

    public void setDrivingDirection(DIRECTION drivingDirection) {
        this.drivingDirection = drivingDirection;
    }

    public List<TrainFunction> getFunctions() {
        return functions;
    }

    public void setFunctions(List<TrainFunction> functions) {
        this.functions = functions;
    }
}
