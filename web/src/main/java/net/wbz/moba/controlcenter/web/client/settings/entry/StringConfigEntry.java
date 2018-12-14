package net.wbz.moba.controlcenter.web.client.settings.entry;

import com.google.gwt.user.client.ui.Widget;
import net.wbz.moba.controlcenter.web.client.settings.Settings.GROUP;
import net.wbz.moba.controlcenter.web.client.settings.Settings.STORAGE;
import org.gwtbootstrap3.client.ui.TextBox;

/**
 * @author Daniel Tuerk
 */
 class StringConfigEntry extends AbstractConfigEntry<String> {

    private TextBox txt;

     StringConfigEntry(STORAGE storageType, GROUP group, String name, String defaultValue) {
        super(storageType, group, name, defaultValue);
    }

    @Override
    protected void valueChanged() {
        txt.setText(getValue());
    }

    @Override
    protected String convertValueFromString(String input) {
        return input;
    }

    @Override
    protected String convertValueToString(String input) {
        return input;
    }

    @Override
    public Widget createConfigEntryWidget() {
        String textFieldId = "txt" + getName();

        txt = new TextBox();
        txt.setId(textFieldId);

        return txt;
    }

    @Override
    protected String getInputValue() {
        return txt.getValue();
    }

}
