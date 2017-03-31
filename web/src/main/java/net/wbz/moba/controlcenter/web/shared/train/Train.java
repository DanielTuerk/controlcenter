package net.wbz.moba.controlcenter.web.shared.train;

import com.google.common.base.Objects;
import java.util.Set;

import com.googlecode.jmapper.annotations.JMap;

import net.wbz.moba.controlcenter.web.shared.track.model.AbstractDto;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackBlock;

/**
 * @author Daniel Tuerk
 */
public class Train extends AbstractDto {

    /**
     * Driving direction of the train.
     */
    public enum DRIVING_DIRECTION {
        FORWARD, BACKWARD
    }

    @JMap
    private Integer address;
    @JMap
    private String name;

    @JMap
    private Set<TrainFunction> functions;

    private int drivingLevel = 0;

    private boolean forward;
    private TrackBlock currentBlock;

    public Integer getAddress() {
        return address;
    }

    public void setAddress(Integer address) {
        this.address = address;
    }

    public byte getAddressByte() {
        return address != null ? address.byteValue() : (byte) -1;
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

    public boolean isForward() {
        return forward;
    }

    public void setForward(boolean forward) {
        this.forward = forward;
    }

    public Set<TrainFunction> getFunctions() {
        return functions;
    }

    public void setFunctions(Set<TrainFunction> functions) {
        this.functions = functions;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("name", name)
            .add("address", address)
                .add("currentBlock", currentBlock)
            .toString();
    }

    public void setCurrentBlock(TrackBlock currentBlock) {
        this.currentBlock = currentBlock;
    }

    public TrackBlock getCurrentBlock() {
        return currentBlock;
    }
}
