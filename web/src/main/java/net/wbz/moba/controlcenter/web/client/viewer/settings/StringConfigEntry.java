package net.wbz.moba.controlcenter.web.client.viewer.settings;

import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.TextBox;

import com.google.gwt.user.client.ui.Widget;

/**
 * @author Daniel Tuerk
 */
public class StringConfigEntry extends AbstractConfigEntry<String> {

    private TextBox txt;

    public StringConfigEntry(STORAGE storageType, String group, String name, String defaultValue) {
        super(storageType, group, name, defaultValue);
    }

    @Override
    protected void valueChanged() {
        txt.setText(getValue());
    }

    public StringConfigEntry(STORAGE storageType, String group, String name) {
        super(storageType, group, name, "");
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
    protected Widget createConfigEntryWidget() {
        String textFieldId = "txt" + getName();
        FormGroup group = new FormGroup();

        FormLabel lbl = new FormLabel();
        lbl.setText(getName());
        lbl.setFor(textFieldId);
        group.add(lbl);

        txt = new TextBox();
        txt.setId(textFieldId);
        group.add(txt);

        return group;
    }

    @Override
    protected String getInputValue() {
        return txt.getValue();
    }

}
