package net.wbz.moba.controlcenter.web.client.viewer.settings;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Daniel Tuerk
 */
public class BooleanConfigEntry extends AbstractConfigEntry<Boolean> {

    private CheckBox cbx;

    public BooleanConfigEntry(STORAGE storageType, String group, String name, Boolean defaultValue) {
        super(storageType, group, name, defaultValue);
    }

    @Override
    protected void valueChanged() {
        ((CheckBox) getWidget()).setValue(getValue());
    }

    public BooleanConfigEntry(STORAGE storageType, String group, String name) {
        super(storageType, group, name, false);
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
    protected Widget createConfigEntryWidget() {
        cbx = new CheckBox(getName());
        return cbx;
    }

    @Override
    protected Boolean getInputValue() {
        return cbx.getValue();
    }

}
