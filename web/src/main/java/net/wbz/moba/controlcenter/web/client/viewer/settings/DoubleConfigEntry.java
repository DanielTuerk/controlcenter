package net.wbz.moba.controlcenter.web.client.viewer.settings;

import org.gwtbootstrap3.client.ui.TextBox;

import com.google.gwt.user.client.ui.Widget;

/**
 * @author Daniel Tuerk
 */
public class DoubleConfigEntry extends AbstractConfigEntry<Double> {

    private TextBox txt;

    public DoubleConfigEntry(STORAGE storageType, String group, String name, Double defaultValue) {
        super(storageType, group, name, defaultValue);
    }

    @Override
    protected void valueChanged() {
        ((TextBox) getWidget()).setText(convertValueToString(getValue()));
    }

    @Override
    protected Double convertValueFromString(String input) {
        return Double.valueOf(input);
    }

    @Override
    protected String convertValueToString(Double input) {
        return String.valueOf(input);
    }

    @Override
    protected Widget createConfigEntryWidget() {
        txt = new TextBox();
        return txt;
    }

    @Override
    protected Double getInputValue() {
        return convertValueFromString(txt.getValue());
    }
}
