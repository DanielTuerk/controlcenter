package net.wbz.moba.controlcenter.web.client.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import org.gwtbootstrap3.extras.select.client.ui.Option;
import org.gwtbootstrap3.extras.select.client.ui.Select;

/**
 * Select component for the model parameter.
 *
 * @author Daniel Tuerk
 */
abstract public class AbstractSelect<T> extends Select {

    private static final String NOTHING_SELECTED = " ---- ";

    private boolean nullableOption = false;

    /**
     * Available choices to select.
     */
    private final List<T> choices = new ArrayList<>();

    /**
     * Initial selected item.
     */
    private T selectedItem = null;

    public void setNullableOption(boolean nullableOption) {
        this.nullableOption = nullableOption;
    }

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
        super.setSelectedValue(getKeyFromNullable(selectedItem));
    }

    public Optional<T> getSelected() {
        for (T choice : choices) {
            if (Objects.equals(getKeyFromNullable(choice), getValue())) {
                return Optional.of(choice);
            }
        }
        return Optional.empty();
    }

    private String getKeyFromNullable(@Nullable T object) {
        return object == null ? null : getKey(object);
    }

    /**
     * Return the key as identifier for the option.
     *
     * @param object object to get key from.
     * @return key
     */
    abstract protected String getKey(@NotNull T object);

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
        return choice == selectedItem || choice.equals(selectedItem);
    }

    /**
     * Create options for the given result.
     *
     * @param result choices
     */
    void initOptions(Collection<T> result) {
        choices.clear();
        clear();
        if (nullableOption) {
            choices.add(null);
        }
        choices.addAll(result);
        for (T choice : choices) {
            Option child = new Option();
            child.setValue(getKeyFromNullable(choice));
            child.setText(choice == null ? NOTHING_SELECTED : getDisplayValue(choice));

            child.setSelected(isSelected(choice));

            add(child);
        }
        refresh();
    }

}
