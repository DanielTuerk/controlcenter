package io.github.danieltuerk.controlcenter.persist.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * @author Daniel Tuerk
 */
@Entity
@Table(name = "CONFIG_VALUE")
public class ConfigValueEntity {

    @Id
    @Column(name = "config_key")
    public String key;

    @Column(name = "config_value", nullable = false)
    public String value;

}
