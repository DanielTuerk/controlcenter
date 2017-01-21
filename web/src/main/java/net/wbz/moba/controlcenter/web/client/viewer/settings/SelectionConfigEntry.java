package net.wbz.moba.controlcenter.web.client.viewer.settings;

import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.extras.select.client.ui.Option;
import org.gwtbootstrap3.extras.select.client.ui.Select;

import com.google.gwt.user.client.ui.Widget;

/**
 * Select the text value from dropdown box.
 * <p/>
 * Values of the dropdown are loaded at start.
 *
 * @author Daniel Tuerk
 */
abstract public class SelectionConfigEntry extends StringConfigEntry {

    private Select select;
    public static String NOTHING_SELECTED = "--";

    public SelectionConfigEntry(STORAGE storageType, String group, String name) {
        super(storageType, group, name, null);
    }

    @Override
    protected void valueChanged() {
        select.setValue(getValue());
        select.refresh();
    }

    public void addOption(String value) {
        Option child = new Option();
        child.setValue(value);
        child.setText(value);
        select.add(child);
        select.refresh();
    }

    @Override
    protected Widget createConfigEntryWidget() {
        String textFieldId = "select" + getName();
        FormGroup group = new FormGroup();

        FormLabel lbl = new FormLabel();
        lbl.setText(getName());
        lbl.setFor(textFieldId);
        group.add(lbl);

        select = new Select();
        select.setId(textFieldId);
        group.add(select);

        return group;
    }

    @Override
    protected String getInputValue() {
        if (!NOTHING_SELECTED.equals(select.getValue())) {
            return select.getValue();
        }
        return null;
    }
}
