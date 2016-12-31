package net.wbz.moba.controlcenter.web.server.persist.config;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * @author Daniel Tuerk
 */
@Entity(name = "CONFIG_VALUE")
public class ConfigValueEntity implements Serializable {

    @Id
    @Column(name = "config_key")
    private String key;

    @Column
    private String value;

    public ConfigValueEntity() {
    }

    public ConfigValueEntity(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public ConfigValueEntity(String key) {
        this.key = key;
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
