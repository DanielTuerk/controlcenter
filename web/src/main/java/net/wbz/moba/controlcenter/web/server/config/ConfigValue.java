package net.wbz.moba.controlcenter.web.server.config;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author Daniel Tuerk
 */
@Entity
public class ConfigValue {

    @Id
    @Column(name = "config_key")
    private String key;
    private String value;

    public ConfigValue() {
    }

    public ConfigValue(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public ConfigValue(String key) {
        this.key=key;
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
}
