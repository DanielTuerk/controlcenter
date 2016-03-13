package net.wbz.moba.controlcenter.web.client.viewer.settings;

import net.wbz.moba.controlcenter.web.client.LocalStorage;
import net.wbz.moba.controlcenter.web.client.ServiceUtils;
import net.wbz.moba.controlcenter.web.client.util.Log;
import net.wbz.moba.controlcenter.web.shared.config.ConfigNotAvailableException;

import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.requestfactory.shared.Receiver;

/**
 * TODO: text for key as name from template
 *
 * @author Daniel Tuerk
 */
abstract public class AbstractConfigEntry<T> {

    private final String group;
    private final String name;
    private final Widget widget;
    private final STORAGE storageType;
    private T value = null;
    private T originalValue;
    private T defaultValue;

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
                try {
                    ServiceUtils.getInstance().getConfigService().loadValue(getConfigKey()).fire(
                            new Receiver<String>() {
                                @Override
                                public void onSuccess(String response) {
                                    handleStorageRead(response);
                                }
                            });
                } catch (ConfigNotAvailableException e) {
                    Log.error(e.getMessage());
                }
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
                setValue(value);
                break;
            case REMOTE:
                ServiceUtils.getInstance().getConfigService().saveValue(getConfigKey(), convertValueToString(value))
                        .fire();
                break;
        }
    }

    protected abstract T getInputValue();

    public void reset() {
        setValue(originalValue);
    }

    public enum STORAGE {
        LOCAL, REMOTE
    }
}
