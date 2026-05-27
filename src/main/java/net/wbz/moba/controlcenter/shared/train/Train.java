package net.wbz.moba.controlcenter.shared.train;

import java.util.HashSet;
import java.util.Set;

import net.wbz.moba.controlcenter.shared.track.model.AbstractDto;
import net.wbz.moba.controlcenter.shared.track.model.TrackBlock;

/**
 * @author Daniel Tuerk
 */
public class Train extends AbstractDto {

    private Integer address;
    private String name;
    private Set<TrainFunction> functions;

    public Integer getAddress() {
        return address;
    }

    public void setAddress(Integer address) {
        this.address = address;
    }

    public byte getAddressByte() {
        return address != null ? address.byteValue() : (byte) -1;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<TrainFunction> getFunctions() {
        return functions != null ? functions : new HashSet<>();
    }

    public void setFunctions(Set<TrainFunction> functions) {
        this.functions = functions;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Train{");
        sb.append("address=").append(address);
        sb.append(", name='").append(name).append('\'');
        sb.append(", functions=").append(functions);
//        sb.append(", drivingLevel=").append(drivingLevel);
//        sb.append(", forward=").append(forward);
        sb.append('}');
        return sb.toString();
    }

    /**
     * Driving direction of the train.
     */
    public enum DRIVING_DIRECTION {
        FORWARD, BACKWARD
    }

}
