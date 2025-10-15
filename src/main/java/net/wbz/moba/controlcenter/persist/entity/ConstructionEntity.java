package net.wbz.moba.controlcenter.persist.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * @author Daniel Tuerk
 */
@Entity
@Table(name = "CONSTRUCTION")
public class ConstructionEntity extends AbstractEntity {

    public String name;

}
