package net.wbz.moba.controlcenter.web.client.viewer.settings;

import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * @author Daniel Tuerk
 */
public class ConfigEntryConstructionManage extends AbstractConstructionConfigEntry {
    @Override
    public String getName() {
        return "Manage";
    }

    @Override
    protected Panel getContentPanel() {
        return new SimplePanel();
    }

    @Override
    protected void save() {

    }
}
