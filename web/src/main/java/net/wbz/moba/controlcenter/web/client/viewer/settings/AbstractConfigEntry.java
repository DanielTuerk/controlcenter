package net.wbz.moba.controlcenter.web.client.viewer.settings;

import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * @author Daniel Tuerk
 */
abstract public class AbstractConfigEntry {

    abstract public String getGroup();
    abstract public String getName();

    abstract protected Panel getContentPanel();

    abstract protected void save();
}
