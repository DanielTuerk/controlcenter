package net.wbz.moba.controlcenter.web.server.persist.train;

import com.google.common.collect.Sets;
import com.googlecode.jmapper.annotations.JMap;
import net.wbz.moba.controlcenter.web.server.persist.AbstractEntity;
import net.wbz.moba.controlcenter.web.shared.train.TrainFunction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * Model for the train.
 * Used for RPC calls and as persistence object.
 *
 * @author Daniel Tuerk
 */
@Entity(name = "train")
public class TrainEntity extends AbstractEntity {


    @JMap
    private Integer address;

    @JMap
    private String name;

    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
//    @JoinTable(joinColumns = {@JoinColumn(name = "ID")}, inverseJoinColumns = {@JoinColumn(name = "TRAINFUNCTION_ID")})
    private Set<TrainFunctionEntity> functions;

//    @Transient
//    private int drivingLevel = 0;
//
//    @Transient
//    private DIRECTION drivingDirection;

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

//    public int getDrivingLevel() {
//        return drivingLevel;
//    }
//
//    public void setDrivingLevel(int drivingLevel) {
//        this.drivingLevel = drivingLevel;
//    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    public DIRECTION getDrivingDirection() {
//        return drivingDirection;
//    }
//
//    public void setDrivingDirection(DIRECTION drivingDirection) {
//        this.drivingDirection = drivingDirection;
//    }
//
    public Set<TrainFunctionEntity> getFunctions() {
        return functions != null ? functions : Sets.<TrainFunctionEntity>newHashSet();
    }

    public void setFunctions(Set<TrainFunctionEntity> functions) {
        this.functions = functions;
    }

    public TrainFunctionEntity getFunction(TrainFunction.FUNCTION function) {
        for (TrainFunctionEntity trainFunction : functions) {
            if (function == trainFunction.getFunction()) {
                return trainFunction;
            }
        }
        return null;
    }

}
