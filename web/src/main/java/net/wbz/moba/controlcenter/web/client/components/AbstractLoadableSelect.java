package net.wbz.moba.controlcenter.web.client.components;

import com.google.gwt.user.client.rpc.AsyncCallback;
import java.util.Collection;
import net.wbz.moba.controlcenter.web.client.Callbacks.OnlySuccessAsyncCallback;

/**
 * Select component for the model parameter.
 * Loading the choices by service call of implementation.
 * 
 * @author Daniel Tuerk
 */
abstract public class AbstractLoadableSelect<T> extends AbstractSelect<T> {

    /**
     * State for loading choices in progress.
     */
    private volatile boolean loading = false;
    /**
     * Item to select which is pending, because loading is running.
     */
    private volatile T pendingToSelect;

    @Override
    protected synchronized void onLoad() {
        loading = true;
        loadChoices(new AsyncCallback<Collection<T>>() {
            @Override
            public void onFailure(Throwable throwable) {
                loading = false;
            }

            @Override
            public void onSuccess(Collection<T> result) {
                initOptions(result);
                loading = false;
                // check for pending select
                if (pendingToSelect != null) {
                    setSelectedItem(pendingToSelect);
                    pendingToSelect = null;
                }
            }
        });
    }

    @Override
    Collection<T> getChoices() {
        // unused
        return null;
    }

    @Override
    public void setSelectedItem(T selectedItem) {
        if (!loading) {
            super.setSelectedItem(selectedItem);
        } else {
            pendingToSelect = selectedItem;
        }
    }

    /**
     * Load the choices with given callback.
     *
     * @param callback {@link OnlySuccessAsyncCallback}
     */
    abstract void loadChoices(AsyncCallback<Collection<T>> callback);

}
