package net.wbz.moba.controlcenter.web.server.persist.train;

import com.google.common.collect.Sets;
import com.googlecode.jmapper.annotations.JMap;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import net.wbz.moba.controlcenter.web.server.persist.AbstractEntity;

/**
 * Model for the train. Used for RPC calls and as persistence object.
 *
 * @author Daniel Tuerk
 */
@Entity(name = "TRAIN")
public class TrainEntity extends AbstractEntity {

    @JMap
    private Integer address;

    @JMap
    private String name;

    @JMap
    @OneToMany(mappedBy = "train", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    private Set<TrainFunctionEntity> functions;

    public TrainEntity() {
    }

    public TrainEntity(String name) {
        this.name = name;
    }

    public Integer getAddress() {
        return address;
    }

    public void setAddress(Integer address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<TrainFunctionEntity> getFunctions() {
        return functions != null ? functions : Sets.newHashSet();
    }

    public void setFunctions(Set<TrainFunctionEntity> functions) {
        this.functions = functions;
    }

}
