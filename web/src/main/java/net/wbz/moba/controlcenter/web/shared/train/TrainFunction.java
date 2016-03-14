package net.wbz.moba.controlcenter.web.shared.train;

import com.google.gwt.user.client.rpc.IsSerializable;
import net.wbz.moba.controlcenter.web.shared.HasVersionAndId;


import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * Function of a {@link net.wbz.moba.controlcenter.web.shared.train.Train}. For each train the functions are created
 * by persist the train. Afterwards the functions could be edit.
 *
 * @author Daniel Tuerk
 */
@Entity
public class TrainFunction implements IsSerializable, Serializable,HasVersionAndId {

    /**
     * Available functions for the train.
     */
    public enum FUNCTION implements Serializable, IsSerializable {LIGHT, HORN, F1, F2, F3, F4, F5, F6, F7, F8}

    @Id
    @GeneratedValue
    @Column(name = "TRAINFUNCTION_ID")
    private long id;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "function_name")
    private FUNCTION function;

    @Transient
    private boolean state;

//    @ManyToMany(mappedBy = "functions", fetch = FetchType.LAZY)
//    private List<Train> trains;

    public TrainFunction() {
    }

    public TrainFunction(FUNCTION function, boolean state) {
        this.function = function;
        this.state = state;
    }

    @Override
    public Integer getVersion() {
        return 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

//    public List<Train> getTrains() {
//        return trains;
//    }
//
//    public void setTrains(List<Train> trains) {
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
