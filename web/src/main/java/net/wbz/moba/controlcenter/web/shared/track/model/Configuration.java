package net.wbz.moba.controlcenter.web.shared.track.model;

import java.io.Serializable;

/**
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
public class Configuration implements Serializable {

    private int adress;

    private int output;

    public int getAdress() {
        return adress;
    }

    public void setAdress(int adress) {
        this.adress = adress;
    }

    public int getOutput() {
        return output;
    }

    public void setOutput(int output) {
        this.output = output;
    }
}
