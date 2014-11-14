package net.wbz.moba.controlcenter.web.client.viewer.settings;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import net.wbz.moba.controlcenter.web.shared.config.ConfigEntryItem;
import org.gwtbootstrap3.client.ui.Form;
import org.gwtbootstrap3.client.ui.constants.FormType;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Daniel Tuerk
 */
abstract public class AbstractConfigEntry {

    private final Map<ConfigEntryItem, Widget> configEntryItems = new HashMap<>();
    private String getConfigGroupKey() {
        return getGroup() + "." + getName();
    }

    private void registerConfigEntry(String name, ConfigEntryItem.ItemType itemType) {
        ConfigEntryItem configEntryItem = new ConfigEntryItem(getConfigGroupKey() + "." + name, itemType);
        configEntryItems.put(configEntryItem, createConfigEntryWidget(configEntryItem));
    }


    private Widget createConfigEntryWidget(ConfigEntryItem configEntryItem) {
        switch (configEntryItem.getItemType()) {

            case BOOLEAN:
                CheckBox cbxShowWelcomePage = new CheckBox(configEntryItem.getKey());
                cbxShowWelcomePage.setValue(Boolean.parseBoolean(configEntryItem.getValue()), false);
                return cbxShowWelcomePage;
            case STRING:
                break;
            case INTEGER:
                break;
        }
        return null;
    }

    public abstract String getGroup();
    public abstract String getName();

    public Panel getContentPanel() {
        Form form = new Form();
        form.setType(FormType.HORIZONTAL);
        for (ConfigEntryItem configEntryItem : configEntryItems.values()) {
            form.add(configEntryItem);
        }
        return form;
    }

    protected abstract void save();

    protected abstract void reset();
}
