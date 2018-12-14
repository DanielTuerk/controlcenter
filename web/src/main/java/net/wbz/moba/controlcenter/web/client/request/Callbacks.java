package net.wbz.moba.controlcenter.web.client.request;

import com.google.gwt.user.client.rpc.AsyncCallback;
import net.wbz.moba.controlcenter.web.client.util.Log;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.extras.notify.client.ui.Notify;

/**
 * Predefined callbacks for the GWT remote access.
 *
 * @author Daniel Tuerk
 */
public final class Callbacks {

    public static final class VoidAsyncCallback extends OnlySuccessAsyncCallback<Void> {

        public VoidAsyncCallback() {
        }

        @Override
        public void onSuccess(Void result) {
            //ignore
        }
    }

    public abstract static class OnlySuccessAsyncCallback<T> implements AsyncCallback<T> {

        private final String errorTitle;
        private final String errorMessage;

        public OnlySuccessAsyncCallback() {
            this("Error", "message");
        }

        public OnlySuccessAsyncCallback(String errorTitle, String errorMessage) {
            this.errorTitle = errorTitle;
            this.errorMessage = errorMessage;
        }

        @Override
        public void onFailure(Throwable caught) {
            Notify.notify(errorTitle, errorMessage + ": " + caught.getMessage(), IconType.WARNING);
            caught.printStackTrace();
            Log.error(caught.getMessage());
        }

    }
}
