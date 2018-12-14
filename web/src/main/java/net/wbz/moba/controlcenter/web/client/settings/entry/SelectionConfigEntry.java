package net.wbz.moba.controlcenter.web.client.settings.entry;

import com.google.gwt.user.client.ui.Widget;
import net.wbz.moba.controlcenter.web.client.settings.Settings.GROUP;
import net.wbz.moba.controlcenter.web.client.settings.Settings.STORAGE;
import org.gwtbootstrap3.extras.select.client.ui.Option;
import org.gwtbootstrap3.extras.select.client.ui.Select;

/**
 * Select the text value from dropdown box.
 * Values of the dropdown are loaded at start.
 *
 * @author Daniel Tuerk
 */
abstract public class SelectionConfigEntry extends StringConfigEntry {

    private Select select;
    static String NOTHING_SELECTED = "--";

    SelectionConfigEntry(STORAGE storageType, GROUP group, String name) {
        super(storageType, group, name, null);
    }

    @Override
    protected void valueChanged() {
        select.setValue(getValue());
        select.refresh();
    }

    void addOption(String value) {
        Option child = new Option();
        child.setValue(value);
        child.setText(value);
        select.add(child);
        select.refresh();
    }

    @Override
    public Widget createConfigEntryWidget() {
        String textFieldId = "select" + getName();
        select = new Select();
        select.setId(textFieldId);
        return select;
    }

    @Override
    protected String getInputValue() {
        if (!NOTHING_SELECTED.equals(select.getValue())) {
            return select.getValue();
        }
        return null;
    }
}
