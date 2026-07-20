package io.github.danieltuerk.controlcenter.persist.entity;

import jakarta.persistence.*;

import java.util.Set;


/**
 * Model for the train. Used for RPC calls and as persistence object.
 *
 * @author Daniel Tuerk
 */
@Entity
@Table(name = "TRAIN")
public class TrainEntity extends AbstractEntity {

    @Column(unique = true)
    public Integer address;

    public String name;

    @OneToMany(mappedBy = "train", cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    public Set<TrainFunctionEntity> functions;

}
