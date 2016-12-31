package net.wbz.moba.controlcenter.web.server.persist.train;

import com.googlecode.jmapper.annotations.JMap;
import net.wbz.moba.controlcenter.web.server.persist.AbstractEntity;
import net.wbz.moba.controlcenter.web.server.persist.construction.track.BusDataConfigurationEntity;
import net.wbz.moba.controlcenter.web.shared.train.TrainFunction;

import javax.persistence.*;

/**
 * Function of a {@link TrainEntity}. For each train the functions are created
 * by persist the train. Afterwards the functions could be edit.
 *
 * @author Daniel Tuerk
 */
@Entity(name = "TRAIN_FUNCTION")
public class TrainFunctionEntity extends AbstractEntity {

    @JMap
    @Column(name = "FUNCTION_ALIAS")
    private String alias;

    @JMap
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private BusDataConfigurationEntity configuration;

    public TrainFunctionEntity() {
    }

    public BusDataConfigurationEntity getConfiguration() {
        return configuration;
    }

    public void setConfiguration(BusDataConfigurationEntity configuration) {
        this.configuration = configuration;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
