package net.wbz.moba.controlcenter.web.client.viewer.settings;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import net.wbz.moba.controlcenter.web.client.LocalStorage;
import net.wbz.moba.controlcenter.web.client.ServiceUtils;
import net.wbz.moba.controlcenter.web.client.util.EmptyCallback;
import net.wbz.moba.controlcenter.web.client.util.Log;

/**
 * @author Daniel Tuerk
 */
abstract public class AbstractConfigEntry<T> {

    private T value = null;
    private T originalValue;
    private T defaultValue;

    private final String group;
    private final String name;

    private final Widget widget;

    public enum STORAGE {LOCAL, REMOTE}

    private final STORAGE storageType;

    public AbstractConfigEntry(STORAGE storageType, String group, String name, T defaultValue) {
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
                ServiceUtils.getConfigService().loadValue(getConfigKey(), new AsyncCallback<String>() {

                    @Override
                    public void onFailure(Throwable throwable) {

                        Log.error(throwable.getMessage());
                    }

                    @Override
                    public void onSuccess(String value) {
                        handleStorageRead(value);
                    }
                });
                break;
        }
    }

    protected void handleStorageRead(String value) {
        if (value != null) {
            T convertedValue = convertValueFromString(value);
            AbstractConfigEntry.this.value = convertedValue;
            AbstractConfigEntry.this.originalValue = convertedValue;
            valueChanged();
        }
    }

    abstract protected void valueChanged();

    abstract protected T convertValueFromString(String input);

    abstract protected String convertValueToString(T input);

    abstract protected Widget createConfigEntryWidget();

    private String getConfigKey() {
        return group + "." + name;
    }

    public T getValue() {
        return value != null ? value : defaultValue;
    }

    public void setValueAndSave(T value) {
        setValue(value);
        save(value);
    }

    public void setValue(T value) {
        this.value = value;
        valueChanged();
    }

    public Widget getWidget() {
        return widget;
    }

    public String getGroup() {
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
                break;
            case REMOTE:
                ServiceUtils.getConfigService().saveValue(getConfigKey(), convertValueToString(value), new EmptyCallback<Void>());
                break;
        }
    }

    protected abstract T getInputValue();

    public void reset() {
        setValue(originalValue);
    }
}
