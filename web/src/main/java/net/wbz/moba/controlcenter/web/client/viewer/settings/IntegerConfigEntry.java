package net.wbz.moba.controlcenter.web.client.viewer.settings;

import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.TextBox;

/**
 * @author Daniel Tuerk
 */
public class IntegerConfigEntry extends AbstractConfigEntry<Integer> {
    private TextBox txt;

    public IntegerConfigEntry(STORAGE storageType,String group, String name, Integer defaultValue) {
        super(storageType, group, name, defaultValue);
    }

    @Override
    protected void valueChanged() {
        ((TextBox) getWidget()).setText(convertValueToString(getValue()));
    }

    @Override
    protected Integer convertValueFromString(String input) {
        return Integer.valueOf(input);
    }

    @Override
    protected String convertValueToString(Integer input) {
        return String.valueOf(input);
    }

    @Override
    protected Widget createConfigEntryWidget() {
        txt = new TextBox();
        return txt;
    }

    @Override
    protected Integer getInputValue() {
        return convertValueFromString(txt.getValue());
    }
}
