package net.wbz.moba.controlcenter.web.shared.train;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.googlecode.jmapper.annotations.JMap;
import net.wbz.moba.controlcenter.web.shared.track.model.AbstractDto;
import org.apache.commons.lang.ArrayUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * @author Daniel Tuerk
 */
public class Train extends AbstractDto {

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

    public void setAddress(Integer address) {
        this.address = address;
    }

    public boolean isForward() {
        return forward;
    }

    public void setForward(boolean forward) {
        this.forward = forward;
    }

    public void setFunctions(Set<TrainFunction> functions) {
        this.functions = functions;
    }

    public Set<TrainFunction> getFunctions() {
        return functions;
    }

}
