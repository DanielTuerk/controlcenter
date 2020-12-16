package net.wbz.moba.controlcenter.web.client.settings.entry;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import net.wbz.moba.controlcenter.web.client.settings.LocalStorage;
import net.wbz.moba.controlcenter.web.client.request.RequestUtils;
import net.wbz.moba.controlcenter.web.client.settings.Settings.GROUP;
import net.wbz.moba.controlcenter.web.client.settings.Settings.STORAGE;

/**
 * TODO: text for key as name from template
 *
 * @author Daniel Tuerk
 */
abstract public class AbstractConfigEntry<T> {

    private final GROUP group;
    private final String name;
    private final Widget widget;
    private final STORAGE storageType;
    private T value = null;
    private T originalValue;
    private T defaultValue;

    AbstractConfigEntry(STORAGE storageType, GROUP group, String name, T defaultValue) {
        this.storageType = storageType;
        this.group = group;
        this.name = name;
        this.defaultValue = defaultValue;
        widget = createConfigEntryWidget();

        switch (storageType) {
            case LOCAL:
                handleStorageRead(LocalStorage.getInstance().get(getConfigKey()));
                break;
            case REMOTE:
                // try {
                RequestUtils.getInstance().getConfigService().loadValue(getConfigKey(), new AsyncCallback<String>() {
                    @Override
                    public void onFailure(Throwable caught) {

                    }

                    @Override
                    public void onSuccess(String result) {
                        handleStorageRead(result);
                    }
                });
                break;
        }
    }

    protected void handleStorageRead(String value) {
        if (value != null) {
            T convertedValue = convertValueFromString(value);
            AbstractConfigEntry.this.originalValue = convertedValue;
            setValue(convertedValue);
        }
    }

    abstract protected void valueChanged();

    abstract protected T convertValueFromString(String input);

    abstract protected String convertValueToString(T input);

    public abstract Widget createConfigEntryWidget();

    private String getConfigKey() {
        return group + "." + name;
    }

    public T getValue() {
        return value != null ? value : defaultValue;
    }

    public void setValue(T value) {
        this.value = value;
        valueChanged();
    }

    public void setValueAndSave(T value) {
        setValue(value);
        save(value);
    }

    public Widget getWidget() {
        return widget;
    }

    public GROUP getGroup() {
        return group;
    }

    public String getName() {
        return name;
    }

    public void save() {
        T inputValue = getInputValue();
        save(inputValue);
    }

    private void save(T value) {
        switch (storageType) {
            case LOCAL:
                LocalStorage.getInstance().set(getConfigKey(), convertValueToString(value));
                setValue(value);
                break;
            case REMOTE:
                RequestUtils.getInstance().getConfigService().saveValue(getConfigKey(), convertValueToString(value),
                        RequestUtils.VOID_ASYNC_CALLBACK);
                break;
        }
        originalValue = value;
    }

    protected abstract T getInputValue();

    public void reset() {
        setValue(originalValue);
    }

}
