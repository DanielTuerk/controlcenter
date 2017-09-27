package net.wbz.moba.controlcenter.web.client.components;

import net.wbz.moba.controlcenter.web.shared.train.Train;

/**
 * Abstract select component for enumerations.
 * 
 * @author Daniel Tuerk
 */
public abstract class AbstractEnumSelect<T extends Enum> extends AbstractSelect<T> {

    @Override
    protected String getKey(T object) {
        return object.name();
    }

    @Override
    protected String getDisplayValue(T choice) {
        return choice.name();
    }
}
