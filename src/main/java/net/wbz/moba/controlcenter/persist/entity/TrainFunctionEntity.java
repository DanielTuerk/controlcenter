package net.wbz.moba.controlcenter.persist.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import net.wbz.moba.controlcenter.persist.entity.track.BusDataConfigurationEntity;

/**
 * Function of a {@link TrainEntity}. For each train the functions are created
 * by persist the train. Afterwards the functions could be edit.
 *
 * @author Daniel Tuerk
 */
@Entity
@Table(name = "TRAIN_FUNCTION")
public class TrainFunctionEntity extends AbstractEntity {

    @Column(name = "FUNCTION_ALIAS")
    public String alias;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    public BusDataConfigurationEntity configuration;

    @ManyToOne
    public TrainEntity train;

}
