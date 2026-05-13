package net.wbz.moba.controlcenter.persist.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


/**
 * @author Daniel Tuerk
 */
@Entity
@Table(name = "SCENARIO_SEQUENCE")
public class RouteSequenceEntity extends AbstractEntity {

    public int position;

    @ManyToOne
    public ScenarioEntity scenario;

    @ManyToOne(fetch = FetchType.EAGER)
    public RouteEntity route;

    /**
     * Seconds to wait after the train arrived on the end block of the route.
     */
    public int endDelayInSeconds;

}
