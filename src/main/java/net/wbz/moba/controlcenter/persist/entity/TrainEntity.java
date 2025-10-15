package net.wbz.moba.controlcenter.persist.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.Set;


/**
 * Model for the train. Used for RPC calls and as persistence object.
 *
 * @author Daniel Tuerk
 */
@Entity
@Table(name = "TRAIN")
public class TrainEntity extends AbstractEntity {

    public Integer address;

    public String name;

    @OneToMany(mappedBy = "train", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    public Set<TrainFunctionEntity> functions;

}
