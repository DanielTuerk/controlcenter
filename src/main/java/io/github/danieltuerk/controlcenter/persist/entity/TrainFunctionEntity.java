package io.github.danieltuerk.controlcenter.persist.entity;

import io.github.danieltuerk.controlcenter.persist.entity.track.BusDataConfigurationEntity;
import jakarta.persistence.*;

/**
 * Function of a {@link TrainEntity}. For each train the functions are created
 * by persisting the train. Afterward the functions could be edited.
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
