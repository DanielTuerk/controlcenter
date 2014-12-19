package net.wbz.moba.controlcenter.web.shared.train;

import com.google.common.collect.Lists;
import net.wbz.moba.controlcenter.web.shared.AbstractIdModel;

import java.util.ArrayList;
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

    private Map<TrainFunction.FUNCTION, TrainFunction> functions;

    private int drivingLevel=0;

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
        return new ArrayList<>(functions.values());
    }

    public TrainFunction getFunction(TrainFunction.FUNCTION function) {
        return functions.get(function);
    }

    public void setFunctions(Map<TrainFunction.FUNCTION, TrainFunction> functions) {
        this.functions = functions;
    }
}
