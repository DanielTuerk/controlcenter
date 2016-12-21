package net.wbz.moba.controlcenter.web.server.persist.train;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.googlecode.jmapper.annotations.JMap;
import net.wbz.moba.controlcenter.web.server.persist.AbstractEntity;
import net.wbz.moba.controlcenter.web.shared.Identity;


import javax.persistence.*;
import java.io.Serializable;

/**
 * Function of a {@link TrainEntity}. For each train the functions are created
 * by persist the train. Afterwards the functions could be edit.
 *
 * @author Daniel Tuerk
 */
@Entity(name = "train_function")
public class TrainFunctionEntity extends AbstractEntity {

    /**
     * Available functions for the train.
     */
    public enum FUNCTION implements Serializable, IsSerializable {LIGHT, HORN, F1, F2, F3, F4, F5, F6, F7, F8}

    @JMap
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "function_name")
    private FUNCTION function;

    @Transient
    private boolean state;

//    @ManyToMany(mappedBy = "functions", fetch = FetchType.LAZY)
//    private List<TrainEntity> trains;

    public TrainFunctionEntity() {
    }

    public TrainFunctionEntity(FUNCTION function, boolean state) {
        this.function = function;
        this.state = state;
    }

//    public List<TrainEntity> getTrains() {
//        return trains;
//    }
//
//    public void setTrains(List<TrainEntity> trains) {
//        this.trains = trains;
//    }

    public FUNCTION getFunction() {
        return function;
    }

    public void setFunction(FUNCTION function) {
        this.function = function;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }
}
