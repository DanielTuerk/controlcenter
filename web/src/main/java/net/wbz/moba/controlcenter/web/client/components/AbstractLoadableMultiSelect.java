package net.wbz.moba.controlcenter.web.client.components;

import com.google.gwt.dom.client.Style.Unit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import net.wbz.moba.controlcenter.web.client.request.Callbacks.OnlySuccessAsyncCallback;
import org.gwtbootstrap3.extras.select.client.ui.MultipleSelect;
import org.gwtbootstrap3.extras.select.client.ui.Option;

/**
 * {@link MultipleSelect} component for the model parameter. Loading the choices by service call of implementation.
 *
 * @param <T> choices of the select
 * @author Daniel Tuerk
 */
abstract public class AbstractLoadableMultiSelect<T> extends MultipleSelect {

    private Collection<T> choices;

    public AbstractLoadableMultiSelect() {
        getElement().getStyle().setMarginTop(10, Unit.PX);
    }

    @Override
    protected void onLoad() {
        super.onLoad();

        clear();

        loadChoices(new OnlySuccessAsyncCallback<Collection<T>>() {
            @Override
            public void onSuccess(Collection<T> result) {
                AbstractLoadableMultiSelect.this.choices = result;
                choices
                    .forEach(entry -> {
                        Option option = new Option();
                        String value = getKeyFromNullable(entry);
                        option.setValue(value);
                        option.setText(getDisplayValue(entry));
                        add(option);
                    });
                refresh();
            }
        });

    }

    public List<T> getSelected() {
        List<Option> selectedItems = getSelectedItems();
        if (selectedItems == null) {
            return new ArrayList<>();
        }

        return selectedItems.stream()
            .map(x -> choices.stream()
                .filter(choice -> Objects.equals(getKey(choice), x.getValue()))
                .findFirst().orElse(null))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    /**
     * Return the display value for the option of the given choice.
     *
     * @param choice {@link T}
     * @return display value
     */
    abstract protected String getDisplayValue(T choice);

    /**
     * Load the choices with given callback.
     *
     * @param callback {@link OnlySuccessAsyncCallback}
     */
    abstract void loadChoices(OnlySuccessAsyncCallback<Collection<T>> callback);

    /**
     * Return the key as identifier for the option.
     *
     * @param object object to get key from.
     * @return key
     */
    abstract protected String getKey(@NotNull T object);

    private String getKeyFromNullable(@Nullable T object) {
        return object == null ? null : getKey(object);
    }

}
