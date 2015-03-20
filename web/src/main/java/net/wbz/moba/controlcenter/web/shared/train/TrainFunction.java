package net.wbz.moba.controlcenter.web.shared.train;

import com.google.gwt.user.client.rpc.IsSerializable;
import net.sf.gilead.pojo.gwt.LightEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

/**
 * Created by Daniel on 08.03.14.
 */
@Entity
public class TrainFunction extends LightEntity implements IsSerializable, Serializable {

    public enum FUNCTION implements Serializable, IsSerializable {LIGHT, HORN, F1, F2, F3, F4, F5, F6, F7, F8}

    @Id
    @Column(name = "function_id")
    @Enumerated(EnumType.ORDINAL)
    private FUNCTION function;

    @Transient
    private boolean state;

    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Train> train;

    public TrainFunction() {
    }

    public TrainFunction(FUNCTION function, boolean state) {
        this.function = function;
        this.state = state;
    }

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
