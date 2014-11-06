package net.wbz.moba.controlcenter.web.client.util;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Callback implementation for no callback handling.
 *
 * @author Daniel Tuerk
 */
public class EmptyCallback<T> implements AsyncCallback<T> {

    @Override
    public void onFailure(Throwable throwable) {

    }

    @Override
    public void onSuccess(T t) {

    }
}
