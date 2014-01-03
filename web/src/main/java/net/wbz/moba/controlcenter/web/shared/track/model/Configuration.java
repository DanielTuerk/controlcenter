package net.wbz.moba.controlcenter.web.shared.track.model;

import java.io.Serializable;

/**
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
public class Configuration implements Serializable {

    private int address;

    private int output;

    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public int getOutput() {
        return output;
    }

    public void setOutput(int output) {
        this.output = output;
    }

    public boolean isValid() {
        return address > -1 && output > -1;
    }

    @Override
    public String toString() {
        return "Configuration{" +
                "address=" + address +
                ", output=" + output +
                '}';
    }
}
