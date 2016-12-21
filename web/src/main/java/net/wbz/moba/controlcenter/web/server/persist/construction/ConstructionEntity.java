package net.wbz.moba.controlcenter.web.server.persist.construction;


import com.googlecode.jmapper.annotations.JMap;
import net.wbz.moba.controlcenter.web.server.persist.AbstractEntity;
import net.wbz.moba.controlcenter.web.shared.Identity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * @author Daniel Tuerk
 */
@Entity(name="Construction")
public class ConstructionEntity extends AbstractEntity {

    @JMap
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
