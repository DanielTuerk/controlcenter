package net.wbz.moba.controlcenter.web.shared.constrution;


import net.wbz.moba.controlcenter.web.shared.HasVersionAndId;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackPart;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackPartFunction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

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
    public Integer getVersion(){return 0;};

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
