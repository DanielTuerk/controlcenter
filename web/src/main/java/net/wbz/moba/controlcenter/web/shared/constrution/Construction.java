package net.wbz.moba.controlcenter.web.shared.constrution;


import net.wbz.moba.controlcenter.web.shared.HasVersionAndId;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * @author Daniel Tuerk
 */
@Entity
public class Construction implements HasVersionAndId {

    @Id
    @GeneratedValue
    private long id;

    private String name;

    @Override
    public Integer getVersion() {
        return 0;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
