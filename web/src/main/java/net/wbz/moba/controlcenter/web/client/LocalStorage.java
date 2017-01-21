package net.wbz.moba.controlcenter.web.client;

import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.extras.notify.client.ui.Notify;

import com.google.gwt.storage.client.Storage;

/**
 * @author Daniel Tuerk
 */
public class LocalStorage {
    public static final String ERROR_TEXT = "no local storage available in this browser";
    private final Storage stockStore;
    private static final LocalStorage INSTANCE = new LocalStorage();

    private LocalStorage() {
        stockStore = Storage.getLocalStorageIfSupported();
        if (stockStore == null) {
            Notify.notify("", ERROR_TEXT, IconType.WARNING);
        }
    }

    public static LocalStorage getInstance() {
        return INSTANCE;
    }

    public String get(String key) {
        checkAvailable();
        return stockStore.getItem(key);
    }

    public void set(String key, String value) {
        checkAvailable();
        stockStore.setItem(key, value);
    }

    private void checkAvailable() {
        if (stockStore == null) {
            throw new RuntimeException(ERROR_TEXT);
        }
    }
}
