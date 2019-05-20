package net.wbz.moba.controlcenter.web.shared.train;

import com.google.common.collect.Sets;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.googlecode.jmapper.annotations.JMap;
import java.util.HashSet;
import java.util.Set;
import net.wbz.moba.controlcenter.web.shared.track.model.AbstractDto;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackBlock;

/**
 * @author Daniel Tuerk
 */
public class Train extends AbstractDto {

    /**
     * Set of {@link TrackBlock}s which the train is actual located.
     */
    private Set<TrackBlock> currentBlocks = new HashSet<>();
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
        return functions != null ? functions : new HashSet<>();
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

    public boolean isCurrentlyInBlock(TrackBlock... trackBlock) {
        return currentBlocks.stream().anyMatch(Sets.newHashSet(trackBlock)::contains);
    }

    public boolean isPresentOnTrack() {
        return !currentBlocks.isEmpty();
    }

    public Set<TrackBlock> getCurrentBlocks() {
        return currentBlocks;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Train{");
        sb.append("currentBlocks=").append(currentBlocks);
        sb.append(", address=").append(address);
        sb.append(", name='").append(name).append('\'');
        sb.append(", functions=").append(functions);
        sb.append(", drivingLevel=").append(drivingLevel);
        sb.append(", forward=").append(forward);
        sb.append('}');
        return sb.toString();
    }

    /**
     * Driving direction of the train.
     */
    public enum DRIVING_DIRECTION implements IsSerializable {
        FORWARD, BACKWARD
    }

}
