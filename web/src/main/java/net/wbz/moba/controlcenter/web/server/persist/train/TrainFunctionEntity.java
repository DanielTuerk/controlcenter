package net.wbz.moba.controlcenter.web.server.persist.train;

import com.googlecode.jmapper.annotations.JMap;
import net.wbz.moba.controlcenter.web.server.persist.AbstractEntity;
import net.wbz.moba.controlcenter.web.shared.train.TrainFunction;

import javax.persistence.*;

/**
 * Function of a {@link TrainEntity}. For each train the functions are created
 * by persist the train. Afterwards the functions could be edit.
 *
 * @author Daniel Tuerk
 */
@Entity(name = "train_function")
public class TrainFunctionEntity extends AbstractEntity {


    /**
     * The mapped function key for the train.
     */
    @JMap
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "function_name")
    private TrainFunction.FUNCTION function;

    @JMap
    @Column(name = "function_alias")
    private String alias;

    /**
     * TODO address, bit
     */

//    @Transient
//    private boolean state;

//    @ManyToMany(mappedBy = "functions", fetch = FetchType.LAZY)
//    private List<TrainEntity> trains;

    public TrainFunctionEntity() {
    }

//    public TrainFunctionEntity(TrainFunction.FUNCTION function, boolean state) {
//        this.function = function;
//        this.state = state;
//    }

//    public List<TrainEntity> getTrains() {
//        return trains;
//    }
//
//    public void setTrains(List<TrainEntity> trains) {
//        this.trains = trains;
//    }

    public TrainFunction.FUNCTION getFunction() {
        return function;
    }

    public void setFunction(TrainFunction.FUNCTION function) {
        this.function = function;
    }

//    public boolean isState() {
//        return state;
//    }
//
//    public void setState(boolean state) {
//        this.state = state;
//    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
