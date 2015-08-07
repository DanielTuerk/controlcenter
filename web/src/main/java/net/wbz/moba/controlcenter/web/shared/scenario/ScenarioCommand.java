package net.wbz.moba.controlcenter.web.shared.scenario;

import net.wbz.moba.controlcenter.web.shared.StringUtils;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
public abstract class ScenarioCommand implements Serializable {

    abstract public String getCommand();

    abstract public void parseParameters(String... text);

    abstract public String[] convertParameters();

    public String toText() {
        return getCommand() + "(" + StringUtils.join(Arrays.asList(convertParameters()), ",") + ")";
    }
}
