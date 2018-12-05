package net.wbz.moba.controlcenter.web.client.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.gwtbootstrap3.extras.select.client.ui.Option;
import org.gwtbootstrap3.extras.select.client.ui.Select;

/**
 * Select component for the model parameter.
 *
 * @author Daniel Tuerk
 */
abstract public class AbstractSelect<T> extends Select {

    /**
     * Available choices to select.
     */
    private final List<T> choices = new ArrayList<>();

    /**
     * Initial selected item.
     */
    private T selectedItem = null;

    @Override
    protected void onLoad() {
        super.onLoad();
        initOptions(getChoices());
    }

    /**
     * Set the initial selected item.
     *
     * @param selectedItem {@link T}
     */
    public void setSelectedItem(T selectedItem) {
        this.selectedItem = selectedItem;
        String key = selectedItem != null ? getKey(selectedItem) : null;
        super.setSelectedValue(key);
    }

    public Optional<T> getSelected() {
        for (T deviceInfo : choices) {
            if (getKey(deviceInfo).equals(getValue())) {
                return Optional.of(deviceInfo);
            }
        }
        return Optional.empty();
    }

    /**
     * Return the key as identifier for the option.
     *
     * @param object object to get key from.
     * @return key
     */
    abstract protected String getKey(T object);

    /**
     * Returns the available choices.
     *
     * @return choices
     */
    abstract Collection<T> getChoices();

    /**
     * Return the display value for the option of the given choice.
     *
     * @param choice {@link T}
     * @return display value
     */
    abstract protected String getDisplayValue(T choice);

    protected boolean isSelected(T choice) {
        return choice.equals(selectedItem);
    }

    /**
     * Create options for the given result.
     *
     * @param result choices
     */
    protected void initOptions(Collection<T> result) {
        choices.clear();
        clear();
        choices.addAll(result);
        for (T choice : choices) {
            Option child = new Option();
            child.setValue(getKey(choice));
            child.setText(getDisplayValue(choice));

            child.setSelected(isSelected(choice));

            add(child);
        }
        refresh();
    }

}
