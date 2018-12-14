package net.wbz.moba.controlcenter.web.client.components;

import javax.validation.constraints.NotNull;

/**
 * Abstract select component for enumerations.
 * 
 * @author Daniel Tuerk
 */
public abstract class AbstractEnumSelect<T extends Enum> extends AbstractSelect<T> {

    @Override
    protected String getKey(@NotNull T object) {
        return object.name();
    }

    @Override
    protected String getDisplayValue(T choice) {
        return choice.name();
    }
}
