package net.wbz.moba.controlcenter.web.shared;

import java.io.Serializable;

/**
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
public class BusData implements Serializable {

    private int address;
    private int value;

    public BusData() {
    }

    public BusData(int address, int value) {
        this.address = address;
        this.value = value;
    }

    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
