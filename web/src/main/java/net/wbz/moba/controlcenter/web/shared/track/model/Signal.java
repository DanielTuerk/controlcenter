package net.wbz.moba.controlcenter.web.shared.track.model;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
public class Signal extends Straight {

    public enum LIGHT {RED1, RED2, GREEN1, GREEN2, YELLOW1, YELLOW2, WHITE}

    public enum TYPE {
        BLOCK(new LIGHT[]{LIGHT.RED1, LIGHT.GREEN1}),
        ENTER(new LIGHT[]{LIGHT.RED1, LIGHT.GREEN1, LIGHT.YELLOW1}),
        EXIT(new LIGHT[]{LIGHT.RED1, LIGHT.RED2, LIGHT.GREEN1, LIGHT.YELLOW1, LIGHT.WHITE}),
        BEFORE(new LIGHT[]{LIGHT.GREEN1, LIGHT.GREEN2, LIGHT.YELLOW1, LIGHT.YELLOW2});

        private LIGHT[] lights;

        TYPE(LIGHT[] lights) {
            this.lights = lights;
        }

        public LIGHT[] getLights() {
            return lights;
        }
    }

    private TYPE type;

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
