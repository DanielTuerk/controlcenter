package net.wbz.moba.controlcenter.web.shared.scenario;

import com.google.common.collect.Lists;
import com.google.gwt.user.client.rpc.IsSerializable;
import net.wbz.moba.controlcenter.web.shared.StringUtils;

/**
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
public abstract class ScenarioCommand implements IsSerializable {

    abstract public String getCommand();

    abstract public void parseParameters(String... text);

    abstract public String[] convertParameters();

    public String toText() {
        return getCommand() + "(" + StringUtils.join(Lists.newArrayList(convertParameters()), ",") + ")";
    }
}
