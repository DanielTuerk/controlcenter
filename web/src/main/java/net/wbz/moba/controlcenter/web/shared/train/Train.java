package net.wbz.moba.controlcenter.web.shared.train;

import net.wbz.moba.controlcenter.web.shared.AbstractIdModel;

import javax.persistence.*;
import java.util.List;

/**
 * Model for the train.
 * Used for RPC calls and as persistence object.
 *
 * @author Daniel Tuerk
 */
@Entity
public class Train extends AbstractIdModel {

    @Id
    @GeneratedValue
    private long id;

    private String name;

    private int address = -1;

    @Access(AccessType.PROPERTY)
    @ManyToMany(targetEntity = TrainFunction.class, mappedBy = "train", fetch = FetchType.EAGER)
    private List<TrainFunction> functions;

    @Transient
    private int drivingLevel = 0;

    public enum DIRECTION {BACKWARD, FORWARD}

    @Transient
    private DIRECTION drivingDirection;

    public Train() {
    }

    public long getId() {
        return id;
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

    public TrainFunction getFunction(TrainFunction.FUNCTION function) {
        for (TrainFunction trainFunction : functions) {
            if (function == trainFunction.getFunction()) {
                return trainFunction;
            }
        }
        return null;
    }

    public void setFunctions(List<TrainFunction> functions) {
        this.functions = functions;
    }
}
