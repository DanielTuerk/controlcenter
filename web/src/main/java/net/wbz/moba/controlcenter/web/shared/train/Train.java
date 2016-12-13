package net.wbz.moba.controlcenter.web.shared.train;

import com.google.common.collect.Sets;
import com.google.gwt.user.client.rpc.IsSerializable;
import net.wbz.moba.controlcenter.web.shared.Identity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Set;

/**
 * Model for the train.
 * Used for RPC calls and as persistence object.
 *
 * @author Daniel Tuerk
 */
@Entity
public class Train implements IsSerializable, Serializable, Identity {

    @Id
    @GeneratedValue
    @Column(name = "TRAIN_ID")
    private long id;

    private String name;

    private int address = -1;

    @NotNull
    @ManyToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @JoinTable(joinColumns = {@JoinColumn(name = "TRAIN_ID")}, inverseJoinColumns = {@JoinColumn(name = "TRAINFUNCTION_ID")})
    private Set<TrainFunction> functions;

    @Transient
    private int drivingLevel = 0;
    @Transient
    private DIRECTION drivingDirection;

    public Train() {
    }

    public Train(String name) {
        this.name = name;
    }

    @Override
    public Integer getVersion() {
        return 0;
    }

    public Long getId() {
        return id;
    }

    protected void setId(long id) {
        this.id = id;
    }

    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public int getDrivingLevel() {
        return drivingLevel;
    }

    public void setDrivingLevel(int drivingLevel) {
        this.drivingLevel = drivingLevel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DIRECTION getDrivingDirection() {
        return drivingDirection;
    }

    public void setDrivingDirection(DIRECTION drivingDirection) {
        this.drivingDirection = drivingDirection;
    }

    public Set<TrainFunction> getFunctions() {
        return functions != null ? functions : Sets.<TrainFunction>newHashSet();
    }

    public void setFunctions(Set<TrainFunction> functions) {
        this.functions = functions;
    }

    public TrainFunction getFunction(TrainFunction.FUNCTION function) {
        for (TrainFunction trainFunction : functions) {
            if (function == trainFunction.getFunction()) {
                return trainFunction;
            }
        }
        return null;
    }

    public enum DIRECTION {BACKWARD, FORWARD}
}
