package net.wbz.moba.controlcenter.web.shared.track.model;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
public class Signal extends Straight {


    public enum TYPE {BLOCK, ENTER, EXIT, BEFORE;}
    private TYPE type;

    public enum FUNCTION {DRIVE, STOP, SLOW, ROUTING}

    private Configuration[] additionalConfigurations;

    public TYPE getType() {
        return type;
    }

    public void setType(TYPE type) {
        this.type = type;
    }

    public Configuration[] getAdditionalConfigurations() {
        return additionalConfigurations;
    }

    public void setAdditionalConfigurations(Configuration[] additionalConfigurations) {
        this.additionalConfigurations = additionalConfigurations;
    }
}
