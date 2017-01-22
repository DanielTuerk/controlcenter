package net.wbz.moba.controlcenter.web.shared.track.model;

import com.google.common.base.Strings;
import com.googlecode.jmapper.annotations.JMap;

import net.wbz.moba.controlcenter.web.shared.constrution.Construction;

/**
 * @author Daniel Tuerk
 */
public class TrackBlock extends AbstractDto {
    @JMap
    private BusDataConfiguration blockFunction;
    @JMap
    private Construction construction;

    @JMap
    private String name;

    public BusDataConfiguration getBlockFunction() {
        return blockFunction;
    }

    public void setBlockFunction(BusDataConfiguration blockFunction) {
        this.blockFunction = blockFunction;
    }

    public Construction getConstruction() {
        return construction;
    }

    public void setConstruction(Construction construction) {
        this.construction = construction;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "TrackBlock{" +
                "blockFunction=" + blockFunction +
                ", name='" + name + '\'' +
                "} " + super.toString();
    }

    public String getDisplayValue() {
        return (Strings.isNullOrEmpty(name) ? "-" : name)
                + " ("
                + (blockFunction != null ? String.valueOf(blockFunction.getAddress()) : "-")
                + ", "
                + (blockFunction != null ? blockFunction.getBit() : "-")
                + ")";
    }
}
