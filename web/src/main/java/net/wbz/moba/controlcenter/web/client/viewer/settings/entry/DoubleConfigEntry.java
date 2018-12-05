package net.wbz.moba.controlcenter.web.client.viewer.settings.entry;

import com.google.gwt.user.client.ui.Widget;
import net.wbz.moba.controlcenter.web.client.Settings.GROUP;
import net.wbz.moba.controlcenter.web.client.Settings.STORAGE;
import org.gwtbootstrap3.client.ui.TextBox;

/**
 * @author Daniel Tuerk
 */
public class DoubleConfigEntry extends AbstractConfigEntry<Double> {

    private TextBox txt;

    public DoubleConfigEntry(STORAGE storageType, GROUP group, String name, Double defaultValue) {
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
    public Widget createConfigEntryWidget() {
        txt = new TextBox();
        return txt;
    }

    @Override
    protected Double getInputValue() {
        return convertValueFromString(txt.getValue());
    }
}
