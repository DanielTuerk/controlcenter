package net.wbz.moba.controlcenter.shared.train;

import lombok.Getter;
import lombok.Setter;
import net.wbz.moba.controlcenter.shared.track.model.AbstractDto;

import java.util.Set;

/**
 * @author Daniel Tuerk
 */
@Getter
@Setter
public class Train extends AbstractDto {

    private Integer address;
    private String name;
    private Set<TrainFunction> functions;

    public byte getAddressByte() {
        return address != null ? address.byteValue() : (byte) -1;
    }

    @Override
    public String toString() {
        return "Train{address=%d, name='%s', functions=%s}".formatted(address, name, functions);
    }

    /**
     * Driving direction of the train.
     */
    public enum DRIVING_DIRECTION {
        FORWARD, BACKWARD
    }

}
