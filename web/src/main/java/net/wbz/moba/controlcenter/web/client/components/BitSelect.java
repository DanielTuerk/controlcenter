package net.wbz.moba.controlcenter.web.client.components;

import java.util.Arrays;
import java.util.Collection;
import javax.validation.constraints.NotNull;

/**
 * Select component for a bit (1 to 8 numeric value).
 *
 * @author Daniel Tuerk
 */
public class BitSelect extends AbstractSelect<Integer> {

    @Override
    protected String getKey(@NotNull Integer object) {
        return String.valueOf(object);
    }

    @Override
    Collection<Integer> getChoices() {
        return Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8);
    }

    @Override
    protected String getDisplayValue(Integer choice) {
        return String.valueOf(choice);
    }

}
