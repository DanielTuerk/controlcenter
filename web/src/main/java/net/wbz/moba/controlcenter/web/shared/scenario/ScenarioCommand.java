package net.wbz.moba.controlcenter.web.shared.scenario;

import com.google.gwt.user.client.rpc.IsSerializable;
import net.wbz.moba.controlcenter.web.shared.StringUtils;

import java.util.Arrays;

/**
 * @author Daniel Tuerk
 */
public abstract class ScenarioCommand implements IsSerializable {

    abstract public String getCommand();

    abstract public void parseParameters(String... text);

    abstract public String[] convertParameters();

    public String toText() {
        return getCommand() + "(" + StringUtils.join(Arrays.asList(convertParameters()), ",") + ")";
    }
}
