package net.wbz.moba.controlcenter.web.server.persist.scenario;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import com.googlecode.jmapper.annotations.JMap;

import net.wbz.moba.controlcenter.web.server.persist.AbstractEntity;

/**
 * @author Daniel Tuerk
 */
@Entity(name = "SCENARIO_SEQUENCE")
public class RouteSequenceEntity extends AbstractEntity {

    @JMap
    @Column
    private int position;

    @ManyToOne
    private ScenarioEntity scenario;

    @JMap
    @ManyToOne
    private RouteEntity route;

    /**
     * Seconds to wait after the train arrived on the end block of the route.
     */
    @JMap
    @Column
    private int endDelayInSeconds;

    public RouteSequenceEntity() {
    }

    public ScenarioEntity getScenario() {
        return scenario;
    }

    public void setScenario(ScenarioEntity scenario) {
        this.scenario = scenario;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public RouteEntity getRoute() {
        return route;
    }

    public void setRoute(RouteEntity route) {
        this.route = route;
    }

    public int getEndDelayInSeconds() {
        return endDelayInSeconds;
    }

    public void setEndDelayInSeconds(int endDelayInSeconds) {
        this.endDelayInSeconds = endDelayInSeconds;
    }
}
