package net.wbz.moba.controlcenter.web.shared.config;

import java.io.Serializable;

/**
 * @author Daniel Tuerk
 */
public class ConfigEntryItem implements Serializable {
    public enum ItemType {BOOLEAN, STRING, INTEGER}
    private String key;
    private String value;
    private ItemType itemType;

    public ConfigEntryItem() {
    }

    public ConfigEntryItem(String key, ItemType itemType) {
        this.key = key;
        this.itemType = itemType;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }

}
