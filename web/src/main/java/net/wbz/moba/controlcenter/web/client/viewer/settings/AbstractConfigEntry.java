package net.wbz.moba.controlcenter.web.client.viewer.settings;

import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * @author Daniel Tuerk
 */
abstract public class AbstractConfigEntry {

    public abstract String getGroup();
    public abstract String getName();

    protected abstract Panel getContentPanel();

    protected abstract void save();

    protected abstract void reset();
}
