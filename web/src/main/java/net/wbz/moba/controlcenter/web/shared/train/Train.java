package net.wbz.moba.controlcenter.web.shared.train;

import java.util.HashSet;
import java.util.Set;

import com.google.common.base.Objects;
import com.googlecode.jmapper.annotations.JMap;

import net.wbz.moba.controlcenter.web.shared.track.model.AbstractDto;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackBlock;

/**
 * @author Daniel Tuerk
 */
public class Train extends AbstractDto {

    /**
     * Set of {@link TrackBlock}s which the train is actual located.
     */
    private final Set<TrackBlock> currentBlocks = new HashSet<>();

    @JMap
    private Integer address;

    @JMap
    private String name;

    @JMap
    private Set<TrainFunction> functions;

    private int drivingLevel = 0;

    private boolean forward;

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

    public synchronized void addCurrentBlock(TrackBlock trackBlock) {
        currentBlocks.add(trackBlock);
    }

    public synchronized void removeCurrentBlock(TrackBlock trackBlock) {
        currentBlocks.remove(trackBlock);
    }

    public boolean isCurrentlyInBlock(TrackBlock trackBlock) {
        return currentBlocks.contains(trackBlock);
    }

    public boolean isPresentOnTrack() {
        return !currentBlocks.isEmpty();
    }

    public Set<TrackBlock> getCurrentBlocks() {
        return currentBlocks;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("name", name)
                .add("address", address)
                .add("currentBlocks", currentBlocks)
                .toString();
    }

    /**
     * Driving direction of the train.
     */
    public enum DRIVING_DIRECTION {
        FORWARD, BACKWARD
    }

}
