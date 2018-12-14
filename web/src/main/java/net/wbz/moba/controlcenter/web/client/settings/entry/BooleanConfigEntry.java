package net.wbz.moba.controlcenter.web.client.settings.entry;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Widget;
import net.wbz.moba.controlcenter.web.client.settings.Settings.GROUP;
import net.wbz.moba.controlcenter.web.client.settings.Settings.STORAGE;

/**
 * @author Daniel Tuerk
 */
public class BooleanConfigEntry extends AbstractConfigEntry<Boolean> {

    private CheckBox cbx;

    public BooleanConfigEntry(STORAGE storageType, GROUP group, String name, Boolean defaultValue) {
        super(storageType, group, name, defaultValue);
    }

    @Override
    protected void valueChanged() {
        cbx.setValue(getValue());
    }

    @Override
    protected Boolean convertValueFromString(String input) {
        return Boolean.parseBoolean(input);
    }

    @Override
    protected String convertValueToString(Boolean input) {
        return String.valueOf(input);
    }

    @Override
    public Widget createConfigEntryWidget() {
        cbx = new CheckBox();
        return cbx;
    }

    @Override
    protected Boolean getInputValue() {
        return cbx.getValue();
    }

}
