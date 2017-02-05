package net.wbz.moba.controlcenter.web.server.persist.scenario;

import javax.persistence.Column;
import javax.persistence.Entity;

import net.wbz.moba.controlcenter.web.server.persist.AbstractEntity;

/**
 * @author Daniel Tuerk
 */
@Entity(name = "SCENARIO")
public class ScenarioEntity extends AbstractEntity {

    @Column
    private String name;
    @Column
    private String cron;

    // // TODO wie den weg finden zwischen stations?
    // @ManyToOne
    // private StationEntity startStation;
    // @OneToMany
    // private List<StationEntity> interstations;
    // @ManyToOne
    // private StationEntity endStation;

}
