package net.wbz.moba.controlcenter.web.client.components;

import java.util.Collection;

import net.wbz.moba.controlcenter.web.client.Callbacks.OnlySuccessAsyncCallback;

/**
 * Select component for the model parameter.
 * Loading the choices by service call of implementation.
 * 
 * @author Daniel Tuerk
 */
abstract public class AbstractLoadableSelect<T> extends AbstractSelect<T> {

    @Override
    protected void onLoad() {
        loadChoices(new OnlySuccessAsyncCallback<Collection<T>>() {
            @Override
            public void onSuccess(Collection<T> result) {
                initOptions(result);
            }
        });

    }

    @Override
    Collection<T> getChoices() {
        // unused
        return null;
    }

    /**
     * Load the choices with given callback.
     *
     * @param callback {@link OnlySuccessAsyncCallback}
     */
    abstract protected void loadChoices(OnlySuccessAsyncCallback<Collection<T>> callback);

}
